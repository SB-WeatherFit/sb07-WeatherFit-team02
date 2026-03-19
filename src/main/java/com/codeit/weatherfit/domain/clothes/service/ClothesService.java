package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDtoCursorResponse;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ClothesService {
    ClothesDto create(ClothesCreateRequest request, MultipartFile image);

    ClothesDto update(UUID clothesId, ClothesUpdateRequest request);

    void delete(UUID clothesId);

    ClothesDtoCursorResponse search(
            UUID ownerId,
            String cursor,
            UUID idAfter,
            ClothesType type,
            int size
    );
}
