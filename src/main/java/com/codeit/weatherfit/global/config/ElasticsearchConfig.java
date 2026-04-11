package com.codeit.weatherfit.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
    basePackages = "com.codeit.weatherfit.domain.feed.repository.search"
)
public class ElasticsearchConfig {
}