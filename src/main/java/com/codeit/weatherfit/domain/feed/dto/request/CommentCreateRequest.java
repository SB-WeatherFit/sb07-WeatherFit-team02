package com.codeit.weatherfit.domain.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentCreateRequest(
        @NotNull
        UUID feedId,

        @NotNull
        UUID authorId,

        @NotBlank(message = "1자 이상 입력해주세요.")
        String content
) {
}
