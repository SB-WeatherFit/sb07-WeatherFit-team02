package com.codeit.weatherfit.domain.feed.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FeedUpdateRequest(
        @NotBlank(message = "내용은 필수입니다.")
        String content
) {
}
