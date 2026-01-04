//package engine.decision;
//
//import engine.behaviour.BehaviorProfile;
//import engine.model.RequestContext;
//
//public class DecisionEngine {
//
//    public DecisionResult evaluate(BehaviorProfile profile, RequestContext context){
//
//        int requestCount = profile.requestCount();
//        double avgRate = profile.averageRatePerMinute();
//
//        double riskScore = calculateRiskScore(profile, context);
//
//        if(riskScore <= 0.4){
//            return new DecisionResult(
//                    Decision.ALLOW,
//                    "Normal traffic pattern",
//                    0,
//                    riskScore
//            );
//        }
//
//        if(riskScore < 0.6 && requestCount > 3){
//            return new DecisionResult(
//                    Decision.THROTTLE,
//                    "Traffic spike detected",
//                    5,
//                    riskScore
//            );
//
//        }
//
//            return new DecisionResult(
//                    Decision.TEMP_BLOCK,
//                    "Repeated abusive pattern detected",
//                    30,
//                    riskScore
//            );
//
//
//
//    }
//
//
//    private double calculateRiskScore(BehaviorProfile profile, RequestContext context){
//
//        double score = 0.0;
//
//        // Behavior-based signals
//        if(profile.exceedsLimit()){
//            score += 0.5;
//        }
//
//        // Endpoint sensitivity
//        switch(context.getType().getRiskLevel()){
//            case HIGH ->  score += 0.4;
//            case MEDIUM -> score += 0.2;
//            case LOW ->  score += 0.1;
//        }
//
//        return Math.min(score, 1.0);
//    }
//}
