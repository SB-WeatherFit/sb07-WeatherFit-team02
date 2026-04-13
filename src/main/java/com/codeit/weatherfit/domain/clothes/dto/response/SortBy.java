package com.codeit.weatherfit.domain.clothes.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "의상 정렬 기준")
public enum SortBy {
    NAME("name"),
    CREATED_AT("createdAt");

    @JsonValue
    private final String value;

    SortBy(String value) {
        this.value = value;
    }
    public String getValue(){
        return this.value;
    }
}
