package engine.decision;

import engine.behaviour.BehaviorProfile;
import engine.model.RequestContext;
import engine.model.RequestType;

public class DecisionEngine {

    public DecisionResult evaluate(BehaviorProfile profile, RequestContext context){

        int requestCount = profile.requestCount();
        double avgRate = profile.averageRatePerMinute();

        double riskScore = calculateRiskScore(profile, context);

        if(riskScore < 0.3){
            return new DecisionResult(
                    Decision.ALLOW,
                    "Normal traffic pattern",
                    0,
                    riskScore
            );
        }

        if(riskScore < 0.6){
            return new DecisionResult(
                    Decision.THROTTLE,
                    "Traffic spike detected",
                    5,
                    riskScore
            );

        }

        if(riskScore < 0.8){
            return new DecisionResult(
                    Decision.TEMP_BLOCK,
                    "Repeated abusive pattern detected",
                    30,
                    riskScore
            );
        }

        return new DecisionResult(
                Decision.HARD_BLOCK,
                "Severe abuse detected",
                120,
                riskScore
        );
    }


    private double calculateRiskScore(BehaviorProfile profile, RequestContext context){

        double score = 0.0;

        // Behavior-based signals
        if(profile.isBursting()){
            score += 0.4;
        }

        if(profile.requestCount() > profile.averageRatePerMinute() * 2){
            score += 0.3;
        }

        // Endpoint sensitivity
        switch(context.getType().getRiskLevel()){
            case HIGH: score += 0.3;
            case MEDIUM: score += 0.15;
            case LOW: score += 0.05;
        }

        return Math.min(score, 1.0);
    }
}
