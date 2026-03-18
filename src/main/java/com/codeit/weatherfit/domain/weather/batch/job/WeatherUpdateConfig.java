package com.codeit.weatherfit.domain.weather.batch.job;

import com.codeit.weatherfit.domain.weather.batch.JobStatus;
import com.codeit.weatherfit.domain.weather.batch.tasklet.WeatherUpdateTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WeatherUpdateConfig {

    private final WeatherUpdateTasklet weatherUpdateTasklet;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobRegistry jobRegistry;

    @Bean
    public Job weatherUpdateJob() {
        return new JobBuilder(JobStatus.WEATHER_UPDATE.getJobName(),jobRepository)
                .start(weatherUpdateStep())
                .build();
    }

    @Bean
    public Step weatherUpdateStep() {
        return new StepBuilder(jobRepository)
                .tasklet(weatherUpdateTasklet,transactionManager)
                .build();
    }
}
