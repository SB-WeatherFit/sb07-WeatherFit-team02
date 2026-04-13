package com.codeit.weatherfit.domain.feed.controller;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.feed.controller.docs.FeedControllerDocs;
import com.codeit.weatherfit.domain.feed.dto.CommentDto;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;
import com.codeit.weatherfit.domain.feed.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController implements FeedControllerDocs {
    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedGetResponse> get(@ModelAttribute @Valid FeedGetRequest request,
                                               @AuthenticationPrincipal WeatherFitUserDetails userDetails) {
        return ResponseEntity.ok(feedService.getFeedsByCursor(request, userDetails));
    }

    @PostMapping
    public ResponseEntity<FeedDto> createFeed(@Valid @ParameterObject @ModelAttribute FeedCreateRequest request,
                                              @AuthenticationPrincipal WeatherFitUserDetails userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                feedService.create(request, userDetails)
        );
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<CommentGetResponse> getComment(@ModelAttribute @Valid CommentGetRequest request,
                                                         @AuthenticationPrincipal WeatherFitUserDetails userDetails) {
        return ResponseEntity.ok(feedService.getCommentsByCursor(request, userDetails));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable UUID id,
                                                    @Valid @ParameterObject @ModelAttribute CommentCreateRequest request,
                                                    @AuthenticationPrincipal WeatherFitUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createComment(id, request, userDetails));
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id,
                                              @PathVariable UUID commentId,
                                              @AuthenticationPrincipal WeatherFitUserDetails userDetails) {
        feedService.deleteComment(id, commentId, userDetails);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(@PathVariable UUID id,
                                     @AuthenticationPrincipal WeatherFitUserDetails userDetails){
        feedService.like(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlike(@PathVariable UUID id,
                                       @AuthenticationPrincipal WeatherFitUserDetails userDetails){
        feedService.unlike(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FeedDto> update(@PathVariable UUID id,
                                          @Valid @ParameterObject @ModelAttribute FeedUpdateRequest request,
                                          @AuthenticationPrincipal WeatherFitUserDetails userDetails) {
        return ResponseEntity.ok(feedService.update(id, request, userDetails));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal WeatherFitUserDetails userDetails) {
        feedService.delete(id, userDetails);
        return ResponseEntity.noContent().build();
    }

}
