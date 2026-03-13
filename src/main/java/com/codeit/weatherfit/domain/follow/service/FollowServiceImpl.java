package com.codeit.weatherfit.domain.follow.service;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService{

    private final FollowRepository followRepository;

    @Override
    public FollowDto follow(FollowCreateRequest createRequest) {
        return null;
    }

    @Override
    public FollowSummaryDto getFollowSummary(UUID userId) {
        return null;
    }

    @Override
    public FollowListResponse getFollowings(FollowerSearchCondition condition) {
        return null;
    }

    @Override
    public FollowListResponse getFollowees(FolloweeSearchCondition condition) {
        return null;
    }

    @Override
    public void unFollow(UUID followId) {

    }
}
