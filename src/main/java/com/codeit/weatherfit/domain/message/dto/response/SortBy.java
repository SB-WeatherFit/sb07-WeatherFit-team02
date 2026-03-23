package com.codeit.weatherfit.domain.message.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SortBy {
    CREATED_AT("createdAt");

    @JsonValue
    private final String value;

    SortBy(String value) {
        this.value = value;
    }
}
