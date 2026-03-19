package com.codeit.weatherfit.domain.notification.dto;

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

    //위처럼 필드에 붙이거나 아래처럼 메서드를 만들어준다.
    //클래스당 하나만!
    //@JsonValue는 객체를 json으로 바꿔줄 때, 객체 전체가 아니라 단일값을 넣어줌
//    @JsonValue
//    public String anything() {
//        return value;
//    }
}
