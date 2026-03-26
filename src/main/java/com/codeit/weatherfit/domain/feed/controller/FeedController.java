package com.codeit.weatherfit.domain.feed.controller;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.feed.dto.CommentDto;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;
import com.codeit.weatherfit.domain.feed.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedGetResponse> get(@RequestBody @Valid FeedGetRequest request) {
        return ResponseEntity.ok(feedService.getFeedsByCursor(request));
    }

    @PostMapping
    public ResponseEntity<FeedDto> createFeed(@RequestBody @Valid FeedCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                feedService.create(request)
        );
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<CommentGetResponse> getComment(@ModelAttribute @Valid CommentGetRequest request) {
        return ResponseEntity.ok(feedService.getCommentsByCursor(request));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable UUID id, @RequestBody @Valid CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createComment(request));
    }


    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(@PathVariable UUID id, @AuthenticationPrincipal WeatherFitUserDetails userDetails){
        feedService.like(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/like") // TODO: 수정
    public ResponseEntity<Void> unlike(@PathVariable UUID id, @AuthenticationPrincipal WeatherFitUserDetails userDetails){
        feedService.unlike(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FeedDto> update(@PathVariable UUID id, @RequestBody @Valid FeedUpdateRequest request) {
        return ResponseEntity.ok(feedService.update(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        feedService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
