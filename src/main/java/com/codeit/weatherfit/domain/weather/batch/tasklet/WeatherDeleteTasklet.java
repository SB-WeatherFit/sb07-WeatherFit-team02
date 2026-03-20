package com.codeit.weatherfit.domain.weather.batch.tasklet;

import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class WeatherDeleteTasklet implements Tasklet {

    private final WeatherRepository weatherRepository;
    private final Executor weatherDeleteTaskExecutor;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        deleteWeather();
        return null;
    }

    public void deleteWeather(){
        Instant targetTime = Instant.now().minus(1, ChronoUnit.HOURS);
        CompletableFuture.runAsync(()->
                        weatherRepository.deleteOlderThen(targetTime)
                ,weatherDeleteTaskExecutor);

        return ;
    }
}
