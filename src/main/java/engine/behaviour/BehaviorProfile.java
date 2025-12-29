package engine.behaviour;

import engine.model.RequestEvent;

import java.util.ArrayDeque;
import java.util.Deque;

public class BehaviorProfile {

    private final String identity;
    private final long windowSizeMillis;
    private final Deque<RequestEvent> recentEvents = new ArrayDeque<>();

    public BehaviorProfile(String identity, long windowSizeMillis) {
        this.identity = identity;
        this.windowSizeMillis = windowSizeMillis;
    }

    public synchronized void record(RequestEvent event){
        recentEvents.addLast(event);
        evictOldEvents(event.getRecordedAt());
    }

    private void evictOldEvents(long now){
        while(!recentEvents.isEmpty()){
            RequestEvent oldest = recentEvents.peekFirst();
            if(now - oldest.getRecordedAt() > windowSizeMillis){
                recentEvents.removeFirst();
            }else{
                break;
            }
        }
    }

    public synchronized int requestCount(){
        return recentEvents.size();
    }

    public synchronized double averageRatePerMinute(){
        return requestCount() * (60_000.0 / windowSizeMillis);
    }

    public synchronized boolean isBursting(){
        return requestCount() > averageRatePerMinute() * 2;
    }
}
