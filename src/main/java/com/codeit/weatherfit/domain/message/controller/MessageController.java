package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageGetResponse;
import com.codeit.weatherfit.domain.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/direct-messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<MessageGetResponse> getMessage(@RequestBody @Valid MessageGetRequest request) {
        return ResponseEntity.ok(messageService.getByCursor(request));
    }
}
