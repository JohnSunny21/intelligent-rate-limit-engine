package engine.model;

public final class RequestEvent {

    private final RequestContext context;
    private final long recordedAt;

    public RequestEvent(RequestContext context, long recordedAt) {
        this.context = context;
        this.recordedAt = recordedAt;
    }

    public RequestContext getContext() {
        return context;
    }

    public long getRecordedAt() {
        return recordedAt;
    }
}
