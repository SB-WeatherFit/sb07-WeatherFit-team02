package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.global.util.ContextCopyingTaskDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    private final ContextCopyingTaskDecorator contextCopyingTaskDecorator;

    @Primary
    @Bean(name = "applicationTaskExecutor")
    public Executor applicationTaskExecutor() {
        return createExecutor("async-", 10, 20, 100);
    }

    @Bean(name = "messageTaskExecutor")
    public Executor messageTaskExecutor() {
        return createExecutor("message-", 20, 40, 500);
    }

    @Bean(name = "weatherUpdateTaskExecutor")
    public Executor weatherUpdateTaskExecutor() {return createExecutor("weather-update-", 20, 40, 500);}

    @Bean(name = "weatherDeleteTaskExecutor")
    public Executor weatherDeleteTaskExecutor() {
        return createExecutor("async-", 10, 20, 100);
    }

    private ThreadPoolTaskExecutor createExecutor(String prefix, int core, int max, int queue) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setThreadNamePrefix(prefix);

        executor.setTaskDecorator(contextCopyingTaskDecorator);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}