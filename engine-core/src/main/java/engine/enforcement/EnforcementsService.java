package engine.enforcement;

import engine.behaviour.BehaviorProfile;
import engine.decision.*;
import engine.model.RequestContext;
import engine.policy.RateLimitPolicy;
import engine.policy.RateLimitRule;
import engine.violation.ViolationRecord;
import engine.violation.ViolationStore;


/**
 * Core enforcement engine.
 *
 * Responsibilities:
 * - Enforce quota-based limits
 * - Handle escalation (TEMP_BLOCK -> HARD_BLOCK)
 * - Ensure forgiveness after cooldown
 * - Prevent infinite punishment loops
 */

public class EnforcementsService {

    private final ViolationStore violationStore;

    public EnforcementsService(ViolationStore violationStore) {
        this.violationStore = violationStore;
    }

    public DecisionResult enforce(String key, BehaviorProfile profile, RequestContext context) {

        long now = System.currentTimeMillis();

        RateLimitRule rule = RateLimitPolicy.ruleFor(context.getType());

        /* ------------------------------------------------------------
         * 1. Handle EXPIRED violations (forgiveness)
         * ------------------------------------------------------------
         * if a violation expired, we:
         * - Remove violation record
         * - Reset request history
         *
         */

        ViolationRecord expired = violationStore.getAndRemoveIfExpired(key, now);

        if(expired != null){
            profile.reset();    // Full forgiveness => reset rate counters
        }

        /* -----------------------------------------------------------
         * 2. Handle ACTIVE violations (block enforcement)
         * -----------------------------------------------------------
         * IMPORTANT:
         * - Do NOT extend block duration endlessly
         * - Do NOT escalate beyond HARD_BLOCK
         */

        ViolationRecord active = violationStore.getActiveViolation(key, now);


        // Escalation path (only if actively blocked)
        if (active != null) {

            // if already HARD_BLOCKED, do not reset timer
            if(active.getViolationCount() >= rule.hardBlockAfter()) {
                long remainingMillis = active.getBlockedUntil() - now;

                return new DecisionResult(
                        Decision.HARD_BLOCK,
                        "Hard blocked due to repeated abuse",
                        (int) (remainingMillis / 1000),
                        1.0
                );
            }

            long remainingMillis = active.getBlockedUntil() - now;

            int newCount = active.getViolationCount() + 1;

            // Decide escalation, but CAP IT
            Decision decision = newCount >= rule.hardBlockAfter()
                    ? Decision.HARD_BLOCK
                    : rule.onViolation();

            // CRITICAL FIX: duration based on decision
            long blockuntill = decision == Decision.HARD_BLOCK
                    ? now + rule.hardBlockMillis() // escalation => new longer block
                    : active.getBlockedUntil();     // TEMP_BLOCK -> keep same timer

            ViolationRecord updated = new ViolationRecord(
                    blockuntill,  // recomputed correctly
                    active.getReason(),
                    newCount
            );

            violationStore.recordViolation(key, updated);


            return new DecisionResult(
                    decision,
                    decision == Decision.HARD_BLOCK
                    ? "Hard blocked due to repeated abuse"
                    : "Rate limit exceeded. Please wait.",
                    (int) (Math.max(0, blockuntill - now) / 1000),
                    1.0
            );
        }

        /* --------------------------------------------------------------
         * 3. Quota check (normal path) -> create first violation
         * --------------------------------------------------------------
         * Count requests within window.
         * If exceeded -> create first violation.
         */
        if (profile.exceedsLimit(rule.maxRequests())) {

            long blockDuration = rule.tempBlockMillis(); // Never windowMillis

            ViolationRecord record = new ViolationRecord(
                    now + blockDuration,
                    "Rate limit exceeded",
                    1
            );

            violationStore.recordViolation(key, record);

            return new DecisionResult(
                    rule.onViolation(),
                    "Rate limit exceeded",
                    (int) (blockDuration / 1000),
                    1.0
            );

        }

        /* --------------------------------------------------------------
         * 4. Allowed request
         * --------------------------------------------------------------
         */

        return new DecisionResult(
                Decision.ALLOW,
                "Allowed",
                0,
                0.0
        );
    }

}
