package com.codeit.weatherfit.domain.notification.event.weather;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record HumidityNotificationEvent(
        UUID receiverId
)  implements PersonalNotificationEvent {
    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle() {
        return "급격한 호우 주의보 발령";
    }

    @Override
    public String getContent() {
        return "급격한 강수량 변화가 관측되었습니다. 갑작스러운 날씨 변화를 유의하세요";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.WARNING;
    }
}
