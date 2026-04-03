package com.codeit.weatherfit.domain.notification.repository;

import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, NotificationCustomRepository {

    long countByReceiverId(UUID receiverId);

    @Query("select n from Notification  n" +
            " join fetch n.receiver " +
            " where n.groupId = :groupId and n.receiver.id in :receiverIds")
    List<Notification> findAllByGroupIdAndReceiverIdIn(
            @Param("groupId") UUID groupId,
            @Param("receiverIds") Collection<UUID> receiverIds
    );
}
