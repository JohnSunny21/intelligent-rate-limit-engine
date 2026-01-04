package com.devtale.ratelimitdemo.config;

import com.devtale.ratelimitdemo.filter.RateLimitFilter;
import engine.behaviour.BehaviorStore;
import engine.enforcement.EnforcementsService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(BehaviorStore behaviorStore, EnforcementsService enforcementsService){
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new RateLimitFilter(behaviorStore, enforcementsService));
        registration.setOrder(1); // the least the first
        registration.addUrlPatterns("/*");

        return registration;
    }


}
