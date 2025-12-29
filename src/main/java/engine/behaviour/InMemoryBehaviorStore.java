package engine.behaviour;

import engine.model.RequestEvent;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBehaviorStore implements BehaviorStore{

    private final ConcurrentHashMap<String, BehaviorProfile> store = new ConcurrentHashMap<>();
    private final long windowSizeMillis;

    public InMemoryBehaviorStore(long windowSizeMillis) {
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public BehaviorProfile getOrCreate(String identity) {
        return store.computeIfAbsent(
                identity,
                id -> new BehaviorProfile(id, windowSizeMillis)
        );
    }

    @Override
    public void record(RequestEvent event) {
        BehaviorProfile profile = getOrCreate(event.getContext().getIdentity());
        profile.record(event);
    }
}
