package com.codeit.weatherfit.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void save(UUID userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
    }

    public boolean existsById(UUID userId) {
        return emitters.containsKey(userId);
    }

    public SseEmitter findByUserId(UUID userId) {
        return emitters.get(userId);
    }

    public void delete(UUID userId) {
        emitters.remove(userId);
    }

    public Map<UUID, SseEmitter> findAll() {
        return Collections.unmodifiableMap(emitters);
    }
}