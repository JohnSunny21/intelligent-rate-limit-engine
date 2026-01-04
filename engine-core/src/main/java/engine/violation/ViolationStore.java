package engine.violation;

public interface ViolationStore {

    void recordViolation(String key, ViolationRecord record);

    ViolationRecord getActiveViolation(String key, long now);

    boolean hasAnyViolation(String key);

    void clear(String key);

    ViolationRecord getAndRemoveIfExpired(String key, long now);
}
