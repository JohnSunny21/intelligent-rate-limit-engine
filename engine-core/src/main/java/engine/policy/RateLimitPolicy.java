package engine.policy;

import engine.decision.Decision;
import engine.model.RequestType;

import java.util.Map;

public class RateLimitPolicy {

    private static final Map<RequestType, RateLimitRule> RULES = Map.of(
            RequestType.LOGIN, new RateLimitRule(5, 60_000, Decision.TEMP_BLOCK, 4, 60_000, 10 * 60_000),
            RequestType.SEARCH, new RateLimitRule(30, 60_000, Decision.THROTTLE,8, 10_000, 5 * 60_000),
            RequestType.ORDER, new RateLimitRule(10, 60_000,Decision.TEMP_BLOCK, 4, 60_000, 10 * 60_000)
    );

    public static RateLimitRule ruleFor(RequestType type){
        return RULES.getOrDefault(
                type,
                new RateLimitRule(20, 60_000, Decision.THROTTLE, 5,10_000, 5 * 60_000)
        );
    }
}
