package com.codeit.weatherfit.global.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "com.codeit.weatherfit")
public class PropertiesScanConfig {
}
