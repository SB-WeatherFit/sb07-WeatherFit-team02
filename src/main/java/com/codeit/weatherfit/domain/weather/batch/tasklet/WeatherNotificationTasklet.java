package com.codeit.weatherfit.domain.weather.batch.tasklet;

import com.codeit.weatherfit.domain.notification.event.weather.HumidityNotificationEvent;
import com.codeit.weatherfit.domain.notification.event.weather.TemperatureNotificationEvent;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherNotificationTasklet implements Tasklet {

    private final WeatherRepository weatherRepository;
    private final Executor weatherNotificationExecutor;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        executeWeatherNotification();
        return RepeatStatus.FINISHED;
    }

    public void executeWeatherNotification(){
        CompletableFuture.runAsync(this::weatherHumidityNotification,weatherNotificationExecutor
                );
        CompletableFuture.runAsync(this::weatherTemperatureNotification,weatherNotificationExecutor
        );
    }

    public void weatherTemperatureNotification() {
        List<UUID> targets = weatherRepository.getTemperatureNotificationTarget();
        targets.forEach(target ->
                eventPublisher.publishEvent(

                        new TemperatureNotificationEvent(
                                target,
                                "급격한 온도 변화가 관측되었습니다. 기온 변화에 유의하세요"
                        )

                )

        );

    }

    public void weatherHumidityNotification() {
        List<UUID> targets = weatherRepository.getHumidityNotificationTarget();
        targets.forEach(x ->
                eventPublisher.publishEvent(
                        new HumidityNotificationEvent(
                                x,
                                "급격한 강수량 변화가 관측되었습니다. 갑작스러운 날씨 변화를 유의하세요"
                        )
                )
        );


    }
}
