package com.codeit.weatherfit.domain.notification.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 레벨", enumAsRef = true)
public enum NotificationLevel {
    INFO ,
    WARNING,
    ERROR,
}
