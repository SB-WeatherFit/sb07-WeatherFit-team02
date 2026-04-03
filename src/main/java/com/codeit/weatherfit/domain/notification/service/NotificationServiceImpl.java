package com.codeit.weatherfit.domain.notification.service;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.repository.NotificationRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public NotificationDto send(UUID receiverId, String title, String content, NotificationLevel level) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 받을 대상이 존재하지 않습니다."));

        Notification notification = Notification.create(receiver, title, content, level);

        Notification savedNotification = notificationRepository.save(notification);

        return NotificationDto.create(savedNotification);
    }

    @Override
    @Transactional
    public UUID broadcast(String title, String content, NotificationLevel level) {
        UUID groupId = UUID.randomUUID();
        List<Notification> notifications = userRepository.findAll().stream()
                .map(user -> Notification.create(user, title, content, level, groupId))
                .toList();

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

        return groupId;
    }

    @Override
    public NotificationCursorResponse search(NotificationSearchCondition condition, UUID userId) {
        List<Notification> notifications = notificationRepository.searchCursor(condition);
        long totalCount = notificationRepository.countByReceiverId(userId);

        Instant nextCursor = null;
        UUID nextIdAfter = null;
        boolean hasNext = false;

        if (notifications.size() > condition.limit()) {
            notifications.removeLast();
            hasNext = true;
            nextCursor = notifications.getLast().getCreatedAt();
            nextIdAfter = notifications.getLast().getId();
        }

        List<NotificationDto> data = notifications.stream()
                .map(NotificationDto::create)
                .toList();

        return new NotificationCursorResponse(data, nextCursor, nextIdAfter, hasNext,  totalCount);
    }

    @Override
    @Transactional
    public void delete(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        notificationRepository.delete(notification);
    }
}
