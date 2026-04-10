package com.codeit.weatherfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = "com.codeit.weatherfit",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*\\.search\\..*"
    )
)
@EnableElasticsearchRepositories(
    basePackages = "com.codeit.weatherfit.domain.feed.repository.search"
)
public class WeatherFitApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherFitApplication.class, args);
    }

}
