package com.codeit.weatherfit.domain.weather.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemScheduler {

    private final BatchScheduler batchScheduler;

}
