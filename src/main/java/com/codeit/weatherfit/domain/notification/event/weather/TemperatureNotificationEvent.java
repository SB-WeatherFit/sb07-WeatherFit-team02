package com.codeit.weatherfit.domain.notification.event.weather;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record TemperatureNotificationEvent(
        UUID receiverId
)  implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle() {
        return "급격한 온도 변화 주의보";
    }

    @Override
    public String getContent() {
        return  "급격한 온도 변화가 관측되었습니다. 기온 변화에 유의하세요";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.WARNING;
    }
}
