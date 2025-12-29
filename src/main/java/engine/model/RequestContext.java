package engine.model;

import java.util.Objects;

public final class RequestContext {

    private final String identity;
    private final String endpoint;
    private final long timestamp;
    private final RequestType type;

    public RequestContext(String identity, String endpoint, long timestamp, RequestType type) {
        this.identity = Objects.requireNonNull(identity, "identity cannot be null");
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint cannot be null");
        this.timestamp = timestamp;
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }


    public String getIdentity() {
        return identity;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public RequestType getType() {
        return type;
    }
}
