package com.devtale.ratelimitdemo.filter;



import engine.behaviour.BehaviorProfile;
import engine.behaviour.BehaviorStore;
import engine.decision.Decision;
import engine.decision.DecisionResult;
import engine.enforcement.EnforcementsService;
import engine.model.RequestContext;
import engine.model.RequestEvent;
import engine.model.RequestType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RateLimitFilter extends OncePerRequestFilter {


    private final BehaviorStore behaviorStore;
    private final EnforcementsService enforcementsService;

    public RateLimitFilter(BehaviorStore behaviorStore, EnforcementsService enforcementsService) {
        this.behaviorStore = behaviorStore;
        this.enforcementsService = enforcementsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String identity = resolveIdentity(request);
        RequestType type = resolveRequestType(request);

        String key = identity + ":" + type.name();

        RequestContext context = new RequestContext(
                identity,
                request.getRequestURI(),
                System.currentTimeMillis(),
                type
        );
        BehaviorProfile profile = behaviorStore.getOrCreate(key);

        // Decide First (NO Recording yet)
        DecisionResult result = enforcementsService.enforce(key, profile, context);
        // Record only NON-Blocked Requests
        if(result.getDecision() == Decision.ALLOW){
            behaviorStore.record(key,
                    new engine.model.RequestEvent(context, System.currentTimeMillis())
            );
            filterChain.doFilter(request, response);
            return;
        }


        handleBlockedResponse(response, result);

    }

    private void handleBlockedResponse(HttpServletResponse response, DecisionResult result) throws IOException{
        if(result.getDecision() == Decision.HARD_BLOCK){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }else{
            response.setStatus(429);
        }

        response.setContentType("application/json");
        response.getWriter().write(
                """
                        {
                        "decision": "%s",
                        "reason": "%s",
                        "retryAfterSeconds": %d
                        }
                        """.formatted(
                                result.getDecision(),
                        result.getReason(),
                        result.getRetryAfterSeconds()
                )
        );
    }

    private String resolveIdentity(HttpServletRequest request){
        return request.getRemoteAddr(); // This is simple for demo purpose.
    }

    private RequestType resolveRequestType(HttpServletRequest request){
        String path = request.getRequestURI();
        if(path.contains("login")) return RequestType.LOGIN;
        if(path.contains("/orders")) return RequestType.ORDER;
        if(path.contains("/search")) return RequestType.SEARCH;

        return RequestType.OTHER;
    }
}
