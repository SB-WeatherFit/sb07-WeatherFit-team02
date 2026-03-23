package com.codeit.weatherfit.domain.clothes.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;

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
