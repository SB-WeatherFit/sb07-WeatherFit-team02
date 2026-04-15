package com.codeit.weatherfit.domain.weather.batch.scheduler;

import com.codeit.weatherfit.domain.weather.batch.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemScheduler {

    private final BatchScheduler batchScheduler;

    @Scheduled(cron = "0 10 2,5,8,11,14,17 * * ?") //2시 5시 8시 11시 14시 17시 10분에 실행
    @SchedulerLock(name = "weatherUpdateJob", lockAtMostFor = "10m")
    public void runWeatherUpdateJob(){
        log.info("Starting weather update job : {}",Instant.now());
        batchScheduler.run(JobStatus.WEATHER_UPDATE.getJobName());
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @SchedulerLock(name = "weatherDeleteJob", lockAtMostFor = "10m")
    public void runWeatherDeleteJob(){
        log.info("Starting weather delete job : {}", Instant.now());
        batchScheduler.run(JobStatus.WEATHER_DELETE.getJobName());
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @SchedulerLock(name = "logUploadJob", lockAtMostFor = "10m")
    public void runLogUploadJob(){
        log.info("Starting log upload job : {}", Instant.now());
        batchScheduler.run(JobStatus.LOG_UPLOAD.getJobName());
    }

    @Scheduled(cron = "0 10 7,8,9,10,11,12,13,14,15,16,17,18,19,20,21 * * ?")
    @SchedulerLock(name = "weatherNotificationJob", lockAtMostFor = "10m")
    public void runWeatherNotificationJob(){
        log.info("Starting weather Notification job : {}",Instant.now());
        batchScheduler.run(JobStatus.WEATHER_NOTIFICATION.getJobName());
    }


}
