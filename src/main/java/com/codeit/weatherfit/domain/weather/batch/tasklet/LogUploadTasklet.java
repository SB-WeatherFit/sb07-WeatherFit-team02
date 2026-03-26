package com.codeit.weatherfit.domain.weather.batch.tasklet;

import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import com.codeit.weatherfit.global.util.s3.LogS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class LogUploadTasklet implements Tasklet {

    private final LogS3Service logS3Service;
    private final Executor s3UploadTaskExecutor;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        s3Upload();
        return null;
    }

    public void s3Upload(){

        CompletableFuture.runAsync(logS3Service::uploadLogFile
                ,s3UploadTaskExecutor);

    }
}
