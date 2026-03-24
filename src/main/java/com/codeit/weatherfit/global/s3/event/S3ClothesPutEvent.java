package com.codeit.weatherfit.global.s3.event;

import java.util.UUID;

public record S3ClothesPutEvent(
        UUID clothesId,
        String fileName,
        String contentType,
        byte[] bytes
) {
}
