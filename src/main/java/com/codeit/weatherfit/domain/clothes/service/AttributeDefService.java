package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefGetRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;

import java.util.List;
import java.util.UUID;

public interface AttributeDefService {
    List<ClothesAttributeDefDto> getAll();
    void deleteAttributeDef (UUID defId);
    ClothesAttributeDefDto patchAttributeDef (UUID defId, ClothesAttributeDefUpdateRequest request);
    List<ClothesAttributeDefDto> getAttributeDefs(ClothesAttributeDefGetRequest request);

}
