package com.codeit.weatherfit.domain.clothes.controller;

import com.codeit.weatherfit.domain.auth.security.JwtAuthenticationFilter;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.service.AttributeDefService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClothesAttributeDefController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClothesAttributeDefControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AttributeDefService attributeDefService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("속성 정의 생성 성공")
    void create_success() throws Exception {
        UUID definitionId = UUID.randomUUID();

        ClothesAttributeDefCreateRequest request =
                new ClothesAttributeDefCreateRequest("색상", List.of("블랙", "화이트"));

        ClothesAttributeDefDto response =
                new ClothesAttributeDefDto(
                        definitionId,
                        "색상",
                        List.of("블랙", "화이트"),
                        Instant.now()
                );

        given(attributeDefService.createAttributeDef(any(ClothesAttributeDefCreateRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/clothes/attribute-defs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(attributeDefService).createAttributeDef(any(ClothesAttributeDefCreateRequest.class));
    }

    @Test
    @DisplayName("속성 정의 목록 조회 성공")
    void getAttributeDefs_success() throws Exception {
        List<ClothesAttributeDefDto> response = List.of(
                new ClothesAttributeDefDto(
                        UUID.randomUUID(),
                        "색상",
                        List.of("블랙", "화이트"),
                        Instant.now()
                ),
                new ClothesAttributeDefDto(
                        UUID.randomUUID(),
                        "소재",
                        List.of("면", "폴리"),
                        Instant.now()
                )
        );

        given(attributeDefService.getAttributeDefs(any()))
                .willReturn(response);

        mockMvc.perform(get("/api/clothes/attribute-defs")
                        .param("sortBy", "name")
                        .param("sortDirection", "ASCENDING"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(attributeDefService).getAttributeDefs(any());
    }

    @Test
    @DisplayName("속성 정의 수정 성공")
    void update_success() throws Exception {
        UUID definitionId = UUID.randomUUID();

        ClothesAttributeDefUpdateRequest request =
                new ClothesAttributeDefUpdateRequest("색상", List.of("네이비", "그레이"));

        ClothesAttributeDefDto response =
                new ClothesAttributeDefDto(
                        definitionId,
                        "색상",
                        List.of("네이비", "그레이"),
                        Instant.now()
                );

        given(attributeDefService.patchAttributeDef(eq(definitionId), any(ClothesAttributeDefUpdateRequest.class)))
                .willReturn(response);

        mockMvc.perform(patch("/api/clothes/attribute-defs/{definitionId}", definitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(attributeDefService).patchAttributeDef(eq(definitionId), any(ClothesAttributeDefUpdateRequest.class));
    }

    @Test
    @DisplayName("속성 정의 삭제 성공")
    void delete_success() throws Exception {
        UUID definitionId = UUID.randomUUID();

        mockMvc.perform(delete("/api/clothes/attribute-defs/{definitionId}", definitionId))
                .andExpect(status().isNoContent());

        verify(attributeDefService).deleteAttributeDef(definitionId);
    }

    @Test
    @DisplayName("속성 정의 생성 실패 - 요청값 검증 실패")
    void create_fail_validation() throws Exception {
        String invalidJson = """
                {
                  "name": "",
                  "selectableValues": []
                }
                """;

        mockMvc.perform(post("/api/clothes/attribute-defs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isInternalServerError());

        verify(attributeDefService, never()).createAttributeDef(any());
    }
}