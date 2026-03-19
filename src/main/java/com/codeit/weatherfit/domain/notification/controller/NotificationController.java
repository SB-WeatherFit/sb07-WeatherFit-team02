package com.codeit.weatherfit.domain.notification.controller;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import com.codeit.weatherfit.domain.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationCursorResponse> findNotifications(
            @ModelAttribute @Valid NotificationSearchCondition condition
//            @Authentication
            ){
        // todo: 인증 정보에서 아이디 가져오기
        UUID randomUUID = UUID.randomUUID();
        NotificationCursorResponse result = notificationService.search(condition, randomUUID);
        return ResponseEntity.ok().body(result);
    }
}
