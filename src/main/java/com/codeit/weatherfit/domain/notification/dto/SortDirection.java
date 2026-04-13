package com.codeit.weatherfit.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "정렬 방향")
public enum SortDirection {
    ASCENDING,
    DESCENDING
}
