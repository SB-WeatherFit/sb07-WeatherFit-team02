package com.codeit.weatherfit.domain.clothes.controller;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDtoCursorResponse;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.clothes.service.ClothesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes")
public class ClothesController {
    private final ClothesService clothesService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClothesDto> create(
            @RequestPart ClothesCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        ClothesDto created = clothesService.create(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping(path = "/{clothesId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClothesDto> update(
            @PathVariable UUID clothesId,
            @RequestBody @Valid ClothesUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ClothesDto response = clothesService.update(clothesId, request, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clothesId}")
    public ResponseEntity<Void> delete(@PathVariable UUID clothesId) {
        clothesService.delete(clothesId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ClothesDtoCursorResponse getClothes(
            @RequestParam UUID ownerId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam(required = false)ClothesType type,
            @RequestParam(defaultValue = "20") int size) {
        return clothesService.search(ownerId, cursor, idAfter, type, size);
    }
}
