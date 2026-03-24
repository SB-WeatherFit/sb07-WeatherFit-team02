package com.codeit.weatherfit.global.util.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class S3Property {

    private String s3AccessKey;
    private String s3SecretKey;
    private String s3Bucket;
    private String s3Region;
    private int s3PresignedUrlExpiration;
}
