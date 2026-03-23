package com.codeit.weatherfit.domain.clothes.controller;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefGetRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.service.AttributeDefService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes/attribute-defs")
public class ClothesAttributeDefController {

    private final AttributeDefService attributeDefService;

    @PostMapping
    public ResponseEntity<Void> create() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ClothesAttributeDefDto> getAll(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
//        return List.of(
//                new ClothesAttributeDefDto(
//                        UUID.randomUUID(),
//                        "색상",
//                        List.of("검정", "흰색", "파랑"),
//                        Instant.now()
//                ),
//                new ClothesAttributeDefDto(
//                        UUID.randomUUID(),
//                        "사이즈",
//                        List.of("S", "M", "L"),
//                        Instant.now()
//                )
//        );
        return attributeDefService.getAll();
    }

    @GetMapping
    public ResponseEntity<List<ClothesAttributeDefDto>> getAttributeDefs(
           @ModelAttribute @Valid ClothesAttributeDefGetRequest request
    ) {
        List<ClothesAttributeDefDto> response = attributeDefService.getAttributeDefs(request);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{definitionId}")
    public ResponseEntity<Void> update(@PathVariable Long definitionId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{definitionId}")
    public ResponseEntity<Void> delete(@PathVariable Long definitionId) {
        return ResponseEntity.noContent().build();
    }
}
