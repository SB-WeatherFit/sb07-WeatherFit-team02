package com.codeit.weatherfit.domain.notification.service.redis;

import java.util.UUID;

public record PersonalEvent(
        UUID receiverId,
        UUID notificationId
) {}
