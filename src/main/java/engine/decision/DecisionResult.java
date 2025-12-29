package engine.decision;

public class DecisionResult {

    private final Decision decision;
    private final String reason;
    private final int retryAfterSeconds;
    private final double riskScore;

    public DecisionResult(Decision decision, String reason, int retryAfterSeconds, double riskScore) {
        this.decision = decision;
        this.reason = reason;
        this.retryAfterSeconds = retryAfterSeconds;
        this.riskScore = riskScore;
    }

    public Decision getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public double getRiskScore() {
        return riskScore;
    }
}
