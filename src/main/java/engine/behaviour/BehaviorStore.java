package engine.behaviour;

import engine.model.RequestEvent;

public interface BehaviorStore {

    BehaviorProfile getOrCreate(String identity);

    void record(RequestEvent event);
}
