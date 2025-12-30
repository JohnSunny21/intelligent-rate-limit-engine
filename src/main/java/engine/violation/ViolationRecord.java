package engine.violation;

public class ViolationRecord {

    private final long blockedUntil;
    private final String reason;

    public ViolationRecord(long blockedUntil, String reason) {
        this.blockedUntil = blockedUntil;
        this.reason = reason;
    }

    public long getBlockedUntil() {
        return blockedUntil;
    }

    public String getReason() {
        return reason;
    }

    public boolean isActive(long now){
        return now < blockedUntil;
    }
}
