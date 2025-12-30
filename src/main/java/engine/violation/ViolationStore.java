package engine.violation;

public interface ViolationStore {

    void recordViolation(String identity, ViolationRecord record);

    ViolationRecord getActiveViolation(String identity, long now);
}
