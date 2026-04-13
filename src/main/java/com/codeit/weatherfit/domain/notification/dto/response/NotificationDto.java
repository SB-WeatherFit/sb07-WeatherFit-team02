package com.codeit.weatherfit.domain.notification.dto.response;

import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        @Schema(description = "알림 ID") UUID id,
        @Schema(description = "생성일시") Instant createdAt,
        @Schema(description = "수신자 ID") UUID receiverId,
        @Schema(description = "알림 제목") String title,
        @Schema(description = "알림 내용") String content,
        @Schema(description = "알림 레벨") NotificationLevel level
) {
    public static NotificationDto create(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getReceiver().getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getLevel()
        );
    }
}
