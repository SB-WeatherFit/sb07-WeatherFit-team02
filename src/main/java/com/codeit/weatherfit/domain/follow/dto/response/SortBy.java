package com.codeit.weatherfit.domain.follow.dto.response;

import lombok.Getter;

@Getter
public enum SortBy {
    CREATED_AT("createdAt");

    private final String value;

    SortBy(String value) {
        this.value = value;
    }
}
