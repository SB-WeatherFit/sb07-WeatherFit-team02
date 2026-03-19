package com.codeit.weatherfit.domain.notification.controller;

import com.codeit.weatherfit.domain.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
//            @AuthenticationPrincipal
//            @RequestParam(value = "lastEventId", required = false) UUID lastEventId
    ) {
        UUID userId =  UUID.randomUUID();
        UUID lastEventId = UUID.randomUUID(); // 어떻게 사용할지 고민

        // 서비스를 통해 연결 생성
        SseEmitter emitter = sseService.connect(userId);

        return ResponseEntity.ok().body(emitter);
    }
}