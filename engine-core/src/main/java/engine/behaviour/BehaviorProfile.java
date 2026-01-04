package engine.behaviour;

import engine.model.RequestEvent;

import java.util.ArrayDeque;
import java.util.Deque;

public class BehaviorProfile {


    private final long windowSizeMillis;
    private final Deque<RequestEvent> recentEvents = new ArrayDeque<>();

    public BehaviorProfile(long windowSizeMillis) {
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

    public synchronized boolean exceedsLimit(int maxRequests){
        // we need enough data before judging
        return recentEvents.size() > maxRequests;
    }

    public synchronized void reset(){
        recentEvents.clear();
    }
}
