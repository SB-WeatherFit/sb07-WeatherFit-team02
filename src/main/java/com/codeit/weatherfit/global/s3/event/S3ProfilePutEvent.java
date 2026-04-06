package com.codeit.weatherfit.global.s3.event;

import java.util.UUID;

public record S3ProfilePutEvent(
        UUID userId,
        String fileName,
        String contentType,
        byte[] bytes
) {
}
