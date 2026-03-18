package com.codeit.weatherfit.domain.weather.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

        private final JobRepository  jobRepository;
        private final JobOperator jobOperator;
        private final JobRegistry jobRegistry;

        public void run(String jobName){

            if(isRunning(jobName)){
                log.warn("Job {} is already running", jobName);
                return;
            }

            try{
                Job job = jobRegistry.getJob(jobName);
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("runAt", System.currentTimeMillis())
                        .toJobParameters();
                jobOperator.start(job,jobParameters);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public boolean isRunning(String jobName){

            return !jobRepository.findRunningJobExecutions(jobName).isEmpty();
        }
}
