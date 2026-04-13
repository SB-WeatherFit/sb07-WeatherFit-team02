package com.codeit.weatherfit.domain.feed.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드 정렬 기준")
public enum SortBy {
    createdAt,
    likeCount
}
