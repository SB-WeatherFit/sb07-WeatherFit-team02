package com.codeit.weatherfit.global.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String accessKey,
        String secretKey,
        String region,
        String bucket,
        Long presignedUrlExpirationTime
) {
}
