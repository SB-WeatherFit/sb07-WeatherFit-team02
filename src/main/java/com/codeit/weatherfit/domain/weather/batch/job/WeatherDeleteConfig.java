package com.codeit.weatherfit.domain.weather.batch.job;

import com.codeit.weatherfit.domain.weather.batch.JobStatus;
import com.codeit.weatherfit.domain.weather.batch.tasklet.WeatherDeleteTasklet;
import com.codeit.weatherfit.domain.weather.batch.tasklet.WeatherUpdateTasklet;
import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;

import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WeatherDeleteConfig {

    private final WeatherDeleteTasklet weatherDeleteTasklet;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job weatherDeleteJob() {
        return new JobBuilder(JobStatus.WEATHER_DELETE.getJobName(),jobRepository)
                .start(weatherDeleteStep())
                .build();
    }

    @Bean
    public Step weatherDeleteStep() {
        return new StepBuilder("weatherDeleteStep", jobRepository)
                .tasklet(weatherDeleteTasklet,transactionManager)
                .build();
    }
}
