package engine.policy;


import engine.decision.Decision;

/**
 *
 * Defines rate-limiting and blocking behaviour per endpoint.
 */
public record RateLimitRule (int maxRequests,
                             long rateWindowMillis,   // Only for counting requests
                             Decision onViolation,
                             int hardBlockAfter ,
                             long tempBlockMillis,      // TEMP_BLOCK duration
                             long hardBlockMillis ){    // HARD_BLOCK duration

}
