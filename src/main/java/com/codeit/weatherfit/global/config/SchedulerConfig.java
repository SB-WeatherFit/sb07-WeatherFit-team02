package com.codeit.weatherfit.global.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulerConfig {

}
