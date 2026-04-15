package com.codeit.weatherfit.domain.weather.batch.job;

import com.codeit.weatherfit.domain.weather.batch.JobStatus;
import com.codeit.weatherfit.domain.weather.batch.tasklet.WeatherNotificationTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WeatherNotificationConfig {

    private final WeatherNotificationTasklet weatherNotificationTasklet;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job weatherNotificationJob() {
        return new JobBuilder(JobStatus.WEATHER_NOTIFICATION.getJobName(),jobRepository)
                .start(weatherNotificationStep())
                .build();
    }

    @Bean
    public Step weatherNotificationStep() {
        return new StepBuilder("weatherNotificationStep", jobRepository)
                .tasklet(weatherNotificationTasklet,transactionManager)
                .build();
    }

}
