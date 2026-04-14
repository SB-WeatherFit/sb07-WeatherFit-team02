package com.codeit.weatherfit;

import com.codeit.weatherfit.domain.feed.repository.search.FeedSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class WeatherFitApplicationTests {

    @MockitoBean
    private FeedSearchRepository feedSearchRepository;

    @Test
    void contextLoads() {
    }

}
