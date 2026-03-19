package com.codeit.weatherfit.domain.notification.entity;

import com.codeit.weatherfit.domain.user.entity.User;

import static com.codeit.weatherfit.domain.message.entity.UserFixture.createUser;

public abstract class NotificationFixture {
    public static Notification createNotification() {
        User user = createUser();
        return Notification.create(user, "title", "content", NotificationLevel.INFO);
    }

    public static Notification createNotification(String title, String content) {
        User user = createUser();
        return Notification.create(user, title, content, NotificationLevel.INFO);
    }
}
