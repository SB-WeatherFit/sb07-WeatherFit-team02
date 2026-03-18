package com.codeit.weatherfit.domain.follow.repository;

import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.entity.Follow;

import java.util.List;

public interface FollowCustomRepository {
    List<Follow> searchFollowees(FolloweeSearchCondition condition);

    List<Follow> searchFollowers(FollowerSearchCondition condition);
}
