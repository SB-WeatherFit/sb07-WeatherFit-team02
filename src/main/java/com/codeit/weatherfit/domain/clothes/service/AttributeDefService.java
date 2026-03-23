package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;

import java.util.List;

public interface AttributeDefService {
    List<ClothesAttributeDefDto> getAll();
}
