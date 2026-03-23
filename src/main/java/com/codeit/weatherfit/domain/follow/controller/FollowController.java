package com.codeit.weatherfit.domain.follow.controller;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.service.FollowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<FollowDto> createFollow(
            @Valid @RequestBody FollowCreateRequest createRequest) {
        FollowDto result = followService.follow(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(result);
    }

    @GetMapping("/summary")
    public ResponseEntity<FollowSummaryDto> getFollowSummary(
            @RequestParam UUID userId
//            @AuthenticationPrincipal    myId
    ){
        UUID myId = UUID.randomUUID(); // todo: Authentication 에서 아이디 가져오는 코드로 변경
        FollowSummaryDto followSummary = followService.getFollowSummary(userId, myId);
        return ResponseEntity.ok(followSummary);
    }

    @GetMapping("/followings")
    public ResponseEntity<FollowListResponse> getFollowings(
            @Valid @ModelAttribute FolloweeSearchCondition condition){
        FollowListResponse result = followService.getFollowees(condition);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/followers")
    public ResponseEntity<FollowListResponse> getFollowers(
            @Valid @ModelAttribute FollowerSearchCondition condition){
        FollowListResponse result = followService.getFollowers(condition);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{followId}")
    public ResponseEntity<Void> unfollow(
            @PathVariable UUID followId
    ){
        followService.unFollow(followId);
        return ResponseEntity.noContent().build();
    }
}
