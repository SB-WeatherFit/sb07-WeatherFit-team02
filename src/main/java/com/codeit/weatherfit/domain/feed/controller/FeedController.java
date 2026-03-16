package com.codeit.weatherfit.domain.feed.controller;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherfit.domain.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController {
    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<FeedDto> createFeed(@RequestBody FeedCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                feedService.create(request)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FeedDto> update(@PathVariable UUID id, @RequestBody FeedUpdateRequest request) {
        return ResponseEntity.ok(feedService.update(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        feedService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
