package engine.enforcement;

import engine.behaviour.BehaviorProfile;
import engine.decision.Decision;
import engine.decision.DecisionEngine;
import engine.decision.DecisionResult;
import engine.model.RequestContext;
import engine.violation.ViolationRecord;
import engine.violation.ViolationStore;

public class EnforcementsService {

    private final DecisionEngine decisionEngine;
    private final ViolationStore violationStore;

    public EnforcementsService(DecisionEngine decisionEngine, ViolationStore violationStore) {
        this.decisionEngine = decisionEngine;
        this.violationStore = violationStore;
    }

    public DecisionResult enforce(BehaviorProfile profile, RequestContext context){

        long now = System.currentTimeMillis();

        // 1. Check active violation
        ViolationRecord active = violationStore.getActiveViolation(context.getIdentity(), now);

        if (active != null){
            return new DecisionResult(
                    Decision.TEMP_BLOCK,
                    "Blocked due to previous violation: " + active.getReason(),
                    (int) ((active.getBlockedUntil() - now) / 1000),
                    1.0
            );
        }

        // 2. Evaluate behavior
        DecisionResult result = decisionEngine.evaluate(profile, context);

        // 3. Record violation if needed
        if(result.getDecision() == Decision.TEMP_BLOCK || result.getDecision() == Decision.HARD_BLOCK){


            long blockDurationMs = result.getRetryAfterSeconds() * 1000L;
            ViolationRecord record = new ViolationRecord(
                    now + blockDurationMs,
                    result.getReason()
            );

            violationStore.recordViolation(context.getIdentity(),record);
        }

        return result;
    }
}
