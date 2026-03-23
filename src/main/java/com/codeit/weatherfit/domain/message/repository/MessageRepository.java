package com.codeit.weatherfit.domain.message.repository;

import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID>, MessageCustomRepository {
    @Query("select count(m.id) from Message m" +
            " where (m.sender.id = :senderId and m.receiver.id = :receiverId) or" +
            "       (m.receiver.id =:senderId and m.sender.id =:receiverId)")
    long countMessage(
            @Param("senderId") UUID senderId,
            @Param("receiverId") UUID receiverId);
}
