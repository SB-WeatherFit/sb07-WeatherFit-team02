package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortByConverter implements Converter<String, SortBy> {

    @Override
    public SortBy convert(String source) {
        for (SortBy sortBy : SortBy.values()) {
            if (sortBy.getValue().equalsIgnoreCase(source)) {
                return sortBy;
            }
        }
        throw new IllegalArgumentException("Invalid SortBy: " + source);
    }
}