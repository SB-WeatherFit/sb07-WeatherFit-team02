package com.codeit.weatherfit.domain.clothes.controller;

import com.codeit.weatherfit.domain.clothes.controller.docs.ClothesAttributeDefControllerDocs;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefGetRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.service.AttributeDefService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes/attribute-defs")
public class ClothesAttributeDefController implements ClothesAttributeDefControllerDocs {

    private final AttributeDefService attributeDefService;

    @PostMapping
    public ResponseEntity<ClothesAttributeDefDto> create(
            @RequestBody @Valid ClothesAttributeDefCreateRequest request
    ) {

        ClothesAttributeDefDto response = attributeDefService.createAttributeDef(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<ClothesAttributeDefDto>> getAttributeDefs(
            @ModelAttribute @Valid ClothesAttributeDefGetRequest request
    ) {
        List<ClothesAttributeDefDto> response = attributeDefService.getAttributeDefs(request);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{definitionId}")
    public ResponseEntity<ClothesAttributeDefDto> update(
            @PathVariable UUID definitionId,
            @RequestBody @Valid ClothesAttributeDefUpdateRequest request
            ) {

        ClothesAttributeDefDto response = attributeDefService.patchAttributeDef(definitionId, request);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{definitionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID definitionId) {

        attributeDefService.deleteAttributeDef(definitionId);
        return ResponseEntity.noContent().build();
    }
}
