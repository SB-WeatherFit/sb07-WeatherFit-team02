package com.codeit.weatherfit.domain.notification.entity;

import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import static com.codeit.weatherfit.domain.notification.entity.NotificationLevel.INFO;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationTest {

    @Test
    void create() {
        User user = UserFixture.create();
        Notification notification = Notification.create(user, "title", "content", INFO);

        assertThat(notification.getTitle()).isEqualTo("title");
        assertThat(notification.getContent()).isEqualTo("content");
    }

    @Test
    void createFail() {
        assertThatThrownBy(()-> Notification.create(null, "title", "content", INFO))
        .isInstanceOf(IllegalArgumentException.class);

        User user = UserFixture.create();
        assertThatThrownBy(()-> Notification.create(user, " ", "content", INFO))
        .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(()-> Notification.create(user, "title", " ", INFO))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(()-> Notification.create(user, "title", "content", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}