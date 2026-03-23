package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;

import java.util.List;

public interface ClothesAttributeTypeRepositoryCustom {

    List<ClothesAttributeDefDto> getAttributeDefs(SortBy sortBy,
                                                SortDirection sortDirection,
                                                String keyword);
    List<ClothesAttributeDefDto> getAttributeDefs(SortBy sortBy,
                                                SortDirection sortDirection)
                                                ;

}
