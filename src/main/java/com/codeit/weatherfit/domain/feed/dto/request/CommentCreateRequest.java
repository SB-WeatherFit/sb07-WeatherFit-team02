package com.codeit.weatherfit.domain.feed.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentCreateRequest(
        @Schema(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID feedId,

        @Schema(description = "작성자 ID", example = "123e4567-e89b-12d3-a456-426614174001")
        @NotNull
        UUID authorId,

        @Schema(description = "댓글 내용", example = "좋은 코디네요!")
        @NotBlank(message = "1자 이상 입력해주세요.")
        String content
) {
}
