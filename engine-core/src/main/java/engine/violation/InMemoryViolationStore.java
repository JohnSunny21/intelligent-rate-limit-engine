package engine.violation;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryViolationStore implements ViolationStore{

    private final ConcurrentHashMap<String, ViolationRecord> violations = new ConcurrentHashMap<>();


    @Override
    public void recordViolation(String key, ViolationRecord record) {
        violations.put(key, record);
    }

    @Override
    public ViolationRecord getActiveViolation(String key, long now) {
        ViolationRecord record = violations.get(key);

        if(record == null){
            return null;
        }

        // Expired -> remove and forget completely
        if(!record.isActive(now)){
            // IMPORTANT: remove expired violation
            violations.remove(key);
            return null;
        }
        return record;
    }

    @Override
    public boolean hasAnyViolation(String key) {
        return violations.containsKey(key);
    }

    @Override
    public void clear(String key) {
        violations.remove(key);
    }

    @Override
    public ViolationRecord getAndRemoveIfExpired(String key, long now){
        ViolationRecord record = violations.get(key);

        if(record == null){
            return null;
        }

        if(record != null && !record.isActive(now)){
            violations.remove(key);
            return record;
        }

        return null;
    }
}
