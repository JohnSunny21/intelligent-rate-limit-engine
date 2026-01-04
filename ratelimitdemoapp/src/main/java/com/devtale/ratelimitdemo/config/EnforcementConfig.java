package com.devtale.ratelimitdemo.config;


import engine.enforcement.EnforcementsService;
import engine.violation.ViolationStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnforcementConfig {

    @Bean
    public EnforcementsService enforcementsService(ViolationStore violationStore){
        return new EnforcementsService(
                violationStore
        );
    }
}
