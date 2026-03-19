package com.codeit.weatherfit.domain.notification.repository;

import com.codeit.weatherfit.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {
}
