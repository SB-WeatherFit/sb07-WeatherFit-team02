package com.codeit.weatherfit.domain.notification.controller;

import com.codeit.weatherfit.domain.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal(expression = "userId") UUID userId,
            @RequestParam(value = "lastEventId", required = false) UUID lastEventId
    ) {
        SseEmitter emitter = sseService.connect(userId);

        return ResponseEntity.ok().body(emitter);
    }
}