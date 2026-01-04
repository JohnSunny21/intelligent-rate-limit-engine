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
    public BehaviorProfile getOrCreate(String key) {
        return store.computeIfAbsent(
                key,
                k -> new BehaviorProfile(windowSizeMillis)
        );
    }

    @Override
    public void record(String key,RequestEvent event) {
         getOrCreate(key).record(event);

    }
}
