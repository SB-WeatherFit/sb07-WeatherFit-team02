package com.codeit.weatherfit.domain.follow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record FollowSummaryDto(
        @Schema(description = "대상 사용자 ID") UUID followeeId,
        @Schema(description = "팔로워 수") long followerCount,
        @Schema(description = "팔로잉 수") long followingCount,
        @Schema(description = "내가 팔로우 중인지") boolean followedByMe,
        @Schema(description = "내 팔로우 ID") UUID followedByMeId,
        @Schema(description = "상대방이 나를 팔로우 중인지") boolean followingMe
) {
    public static  FollowSummaryDto create(UUID followeeId,
                                         long followerCount,
                                         long followeeCount,
                                         boolean followedByMe,
                                         UUID followedByMeId,
                                         boolean followingMe ) {
        return new FollowSummaryDto(
                followeeId,
                followerCount,
                followeeCount,
                followedByMe,
                followedByMeId,
                followingMe
        );
    }
}
