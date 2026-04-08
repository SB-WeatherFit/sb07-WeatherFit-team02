package com.codeit.weatherfit.global.s3.event;

import java.util.UUID;

public record S3ProfileDeletedEvent(
        UUID profileId,
        String imageKey
) {
}
