package com.codeit.weatherfit.domain.message.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.message.exception.InvalidMessageArgumentException;
import com.codeit.weatherfit.domain.message.exception.NotSendSelfMessageException;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    String content;

    public static Message create(User sender, User receiver) {
        validateUsersExist(sender, receiver);
        validateNotSendMySelf(sender, receiver);

        Message message = new Message();
        message.sender = sender;
        message.receiver = receiver;

        return message;
    }

    private static void validateUsersExist(User sender, User receiver) {
        if (sender == null || receiver == null) {
            throw new InvalidMessageArgumentException(ErrorCode.INVALID_MESSAGE_ARGUMENT);
        }
    }

    private static void validateNotSendMySelf(User sender, User receiver) {
        UUID senderId = sender.getId();
        UUID receiverId = receiver.getId();

        if (senderId != null && senderId.equals(receiverId)) {
            throw new NotSendSelfMessageException(ErrorCode.NOT_SEND_SELF_MESSAGE);
        }
    }
}
