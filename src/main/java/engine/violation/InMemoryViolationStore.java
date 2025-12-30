package engine.violation;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryViolationStore implements ViolationStore{

    private final ConcurrentHashMap<String, ViolationRecord> violations = new ConcurrentHashMap<>();


    @Override
    public void recordViolation(String identity, ViolationRecord record) {
        violations.put(identity, record);
    }

    @Override
    public ViolationRecord getActiveViolation(String identity, long now) {
        ViolationRecord record = violations.get(identity);
        if(record == null){
            return null;
        }
        return record.isActive(now) ? record : null;
    }
}
