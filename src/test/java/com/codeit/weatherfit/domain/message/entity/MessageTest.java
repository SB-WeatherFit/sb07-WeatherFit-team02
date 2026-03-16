package com.codeit.weatherfit.domain.message.entity;

import com.codeit.weatherfit.domain.message.exception.InvalidMessageArgumentException;
import com.codeit.weatherfit.domain.message.exception.MessageContentNullException;
import com.codeit.weatherfit.domain.message.exception.NotSendSelfMessageException;
import com.codeit.weatherfit.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTest {

    @Test
    void create() {
        User user = UserFixture.create("test1@gmail.com");
        User user2 = UserFixture.create("test2@gmail.com");
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(user2, "id", UUID.randomUUID());
        String content = "이건 내용";

        Message message = Message.create(user, user2, content);

        assertThat(message.content).isEqualTo(content);
        assertThat(message.getSender()).isEqualTo(user);
        assertThat(message.getReceiver()).isEqualTo(user2);
    }

    @Test
    void createFail_UserIdNull() {
        User user = UserFixture.create("test1@gmail.com");
        User user2 = UserFixture.create("test2@gmail.com");
        String content = "이건 내용";

        assertThatThrownBy(() -> Message.create(user, user2, content))
                .isInstanceOf(InvalidMessageArgumentException.class);
    }

    @Test
    void createFail_SendSelfMessage() {
        User user = UserFixture.create("test1@gmail.com");
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        String content = "이건 내용";

        assertThatThrownBy(() -> Message.create(user, user, content))
                .isInstanceOf(NotSendSelfMessageException.class);
    }

    @Test
    void createFail_UserNull() {
        assertThatThrownBy(() -> Message.create(null, null, "내용"))
                .isInstanceOf(InvalidMessageArgumentException.class);
    }

    @Test
    void createFail_ContentNull() {
        User user = UserFixture.create("test1@gmail.com");
        User user2 = UserFixture.create("test2@gmail.com");
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(user2, "id", UUID.randomUUID());
        String content = null;

        assertThatThrownBy(() -> Message.create(user, user2, content))
                .isInstanceOf(MessageContentNullException.class);
    }
}