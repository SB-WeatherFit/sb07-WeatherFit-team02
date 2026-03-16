package com.codeit.weatherfit.domain.feed.controller;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequestDto;
import com.codeit.weatherfit.domain.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController {
    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<FeedDto> createFeed(FeedCreateRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                feedService.create(request)
        );
    }
}
