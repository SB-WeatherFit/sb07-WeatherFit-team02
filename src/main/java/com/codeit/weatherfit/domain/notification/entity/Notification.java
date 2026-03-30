package com.codeit.weatherfit.domain.notification.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationLevel level;

    public static Notification create(User receiver, String title, String content, NotificationLevel level) {
        validateReceiverNull(receiver);
        validateTitle(title);
        validateContent(content);
        validateNotificationLevel(level);

        Notification notification = new Notification();

        notification.receiver =  receiver;
        notification.title = title;
        notification.content = content;
        notification.level = level;

        return notification;
    }

    private static void validateNotificationLevel(NotificationLevel level) {
        if (level == null) {
            throw new  IllegalArgumentException("level cannot be null");
        }
    }

    private static void validateContent(String content) {
        if (content ==null || content.isBlank()){
            throw new IllegalArgumentException("content is blank");
        }
    }

    private static void validateTitle(String title) {
        if (title ==null || title.isBlank()){
            throw new IllegalArgumentException("title is blank");
        }
    }

    private static void validateReceiverNull(User receiver) {
        if (receiver == null) {
            throw new IllegalArgumentException("receiver is required");
        }
    }
}
