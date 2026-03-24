package com.codeit.weatherfit.domain.notification.controller;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import com.codeit.weatherfit.domain.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationCursorResponse> findNotifications(
            @ModelAttribute @Valid NotificationSearchCondition condition,
            @AuthenticationPrincipal(expression = "userId") UUID myId
            ){
        NotificationCursorResponse result = notificationService.search(condition, myId);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID notificationId) {

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
