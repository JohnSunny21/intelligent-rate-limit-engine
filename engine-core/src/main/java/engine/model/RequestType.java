package engine.model;

public enum RequestType {

    LOGIN(EndpointRiskLevel.HIGH),
    SEARCH(EndpointRiskLevel.MEDIUM),
    ORDER(EndpointRiskLevel.HIGH),
    OTHER(EndpointRiskLevel.LOW);

    private final EndpointRiskLevel riskLevel;

    RequestType(EndpointRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public EndpointRiskLevel getRiskLevel() {
        return riskLevel;
    }
}
