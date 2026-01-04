package engine.behaviour;

import engine.model.RequestEvent;

public interface BehaviorStore {

    BehaviorProfile getOrCreate(String key);

    void record(String key, RequestEvent event);
}
