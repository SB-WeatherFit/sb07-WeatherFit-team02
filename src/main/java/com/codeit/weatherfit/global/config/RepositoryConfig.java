package com.codeit.weatherfit.global.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
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
public class RepositoryConfig {
}
