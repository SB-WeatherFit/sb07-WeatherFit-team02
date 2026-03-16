package com.codeit.weatherfit.domain.message.repository;

import com.codeit.weatherfit.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
