package com.codeit.weatherfit.domain.notification.controller;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationsSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import com.codeit.weatherfit.domain.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationCursorResponse> findNotifications(
            @ModelAttribute @Valid NotificationsSearchCondition condition
            ){

        NotificationCursorResponse result = notificationService.search(condition);
        return ResponseEntity.ok().body(result);
    }
}
