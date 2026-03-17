package com.codeit.weatherfit.domain.follow.service;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;

import java.util.UUID;

public interface FollowService {
    FollowDto follow(FollowCreateRequest createRequest);
    FollowSummaryDto getFollowSummary(UUID userId, UUID myId);
    FollowListResponse getFollowees(FolloweeSearchCondition condition);
    FollowListResponse getFollowers(FollowerSearchCondition condition);
    void unFollow(UUID followId);
}
