package com.codeit.weatherfit.domain.notification.repository;

import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {

    long countByReceiverId(UUID receiverId);
}
