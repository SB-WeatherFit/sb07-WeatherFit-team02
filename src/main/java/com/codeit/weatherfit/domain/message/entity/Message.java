package com.codeit.weatherfit.domain.message.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.message.exception.InvalidMessageArgumentException;
import com.codeit.weatherfit.domain.message.exception.MessageContentNullException;
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

    @Column(name = "content", nullable = false)
    String content;

    public static Message create(User sender, User receiver, String content) {
        validateUsersExist(sender, receiver);
        validateNotSendMySelf(sender, receiver);
        validateUserIdNotNull(sender, receiver);
        validateContent(content);

        Message message = new Message();
        message.sender = sender;
        message.receiver = receiver;
        message.content = content;

        return message;
    }

    private static void validateUsersExist(User sender, User receiver) {
        if (sender == null || receiver == null) {
            throw new InvalidMessageArgumentException();
        }
    }

    private static void validateNotSendMySelf(User sender, User receiver) {
        UUID senderId = sender.getId();
        UUID receiverId = receiver.getId();

        if (senderId != null && senderId.equals(receiverId)) {
            throw new NotSendSelfMessageException();
        }
    }

    private static void validateContent(String content) {
        if(content == null){
            throw new MessageContentNullException();
        }
    }

    private static void validateUserIdNotNull(User sender, User receiver) {
        if(sender.getId()==null || receiver.getId() ==null){
            throw new InvalidMessageArgumentException();
        }
    }
}
