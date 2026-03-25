package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;
import com.codeit.weatherfit.domain.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/direct-messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<MessageCursorResponse> getMessage(
            @ModelAttribute @Valid MessageGetRequest request,
            @AuthenticationPrincipal(expression = "userId") UUID myId
    ) {
        return ResponseEntity.ok(messageService.searchMessages(request, myId));
    }
}
