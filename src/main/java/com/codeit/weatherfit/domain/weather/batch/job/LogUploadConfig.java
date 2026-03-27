package com.codeit.weatherfit.domain.weather.batch.job;

import com.codeit.weatherfit.domain.weather.batch.JobStatus;
import com.codeit.weatherfit.domain.weather.batch.tasklet.LogUploadTasklet;
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
public class LogUploadConfig {

    private final LogUploadTasklet logUploadTasklet;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job logUploadJob() {
        return new JobBuilder(JobStatus.LOG_UPLOAD.getJobName(),jobRepository)
                .start(logUploadStep())
                .build();
    }

    @Bean
    public Step logUploadStep() {
        return new StepBuilder("logUploadStep", jobRepository)
                .tasklet(logUploadTasklet,transactionManager)
                .build();
    }
}
