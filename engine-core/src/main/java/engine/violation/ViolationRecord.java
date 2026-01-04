package engine.violation;

/**
 *  Represents a single blocking window.
 */
public class ViolationRecord {

    private final long blockedUntil;
    private final String reason;
    private final int violationCount;

    public ViolationRecord(long blockedUntil, String reason, int violationCount) {
        this.blockedUntil = blockedUntil;
        this.reason = reason;
        this.violationCount = violationCount;
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

    public int getViolationCount() {
        return violationCount;
    }

    public boolean justExpired(long now){
        return now >= blockedUntil;
    }
}
