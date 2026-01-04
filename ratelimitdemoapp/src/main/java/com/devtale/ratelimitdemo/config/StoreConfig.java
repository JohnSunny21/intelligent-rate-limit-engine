package com.devtale.ratelimitdemo.config;

import engine.behaviour.BehaviorStore;
import engine.behaviour.InMemoryBehaviorStore;
import engine.violation.InMemoryViolationStore;
import engine.violation.ViolationStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {

    @Bean
    public BehaviorStore behaviorStore(){
        return new InMemoryBehaviorStore(60_000);
    }

    @Bean
    public ViolationStore violationStore(){
        return new InMemoryViolationStore();
    }
}
