package com.codeit.weatherfit.domain.feed.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record FeedUpdateRequest(
        @Schema(description = "수정할 내용", example = "수정된 피드 내용입니다.")
        @NotBlank(message = "내용은 필수입니다.")
        String content
) {
}
