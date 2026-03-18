package com.codeit.weatherfit.domain.weather.batch.scheduler;

import com.codeit.weatherfit.domain.weather.batch.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemScheduler {

    private final BatchScheduler batchScheduler;

    @Scheduled(cron = "0 10 2,5,8,11,14,17 * * ?") //2시 5시 8시 11시 14시 17시 10분에 실행
    public void runWeatherUpdateJob(){
        log.info("Starting weather update job : {}",Instant.now());
        batchScheduler.run(JobStatus.WEATHER_UPDATE.getJobName());
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void runWeatherDeleteJob(){
        log.info("Starting weather delete job : {}", Instant.now());
        batchScheduler.run(JobStatus.WEATHER_DELETE.getJobName());
    }




}
