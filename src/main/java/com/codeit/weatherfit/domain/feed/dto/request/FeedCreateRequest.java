package com.codeit.weatherfit.domain.feed.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record FeedCreateRequest(
        @Schema(description = "작성자 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID authorId,
        @Schema(description = "날씨 ID", example = "123e4567-e89b-12d3-a456-426614174001")
        @NotNull
        UUID weatherId,
        @Schema(description = "착용 의상 ID 목록", example = "[\"123e4567-e89b-12d3-a456-426614174002\"]")
        @NotEmpty
        List<UUID> clothesIds,
        @Schema(description = "피드 내용", example = "오늘의 OOTD입니다!")
        @NotBlank
        String content
) {
}