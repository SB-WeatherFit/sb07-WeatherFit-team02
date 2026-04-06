package com.codeit.weatherfit.domain.clothes.controller;

import com.codeit.weatherfit.domain.auth.security.JwtAuthenticationFilter;
import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.clothes.service.ClothesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClothesController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClothesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClothesService clothesService;

    @MockitoBean
    private RequestContextFilter requestContextFilter;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("의상 생성 성공")
    void create_success() throws Exception {
        UUID ownerId = UUID.randomUUID();

        String requestJson = """
                {
                  "ownerId": "%s",
                  "name": "검정 반팔",
                  "type": "TOP",
                  "attributes": [
                    {
                      "definitionId": "%s",
                      "value": "블랙"
                    }
                  ]
                }
                """.formatted(ownerId, UUID.randomUUID());

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image".getBytes(StandardCharsets.UTF_8)
        );

        given(clothesService.create(any(ClothesCreateRequest.class), any(MultipartFile.class)))
                .willReturn(null);

        mockMvc.perform(
                        multipart("/api/clothes")
                                .file(requestPart)
                                .file(imagePart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated());

        verify(clothesService).create(any(ClothesCreateRequest.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("의상 수정 성공")
    void update_success() throws Exception {
        UUID clothesId = UUID.randomUUID();

        String requestJson = """
                {
                  "name": "수정된 검정 반팔",
                  "type": "TOP",
                  "attributes": [
                    {
                      "definitionId": "%s",
                      "value": "네이비"
                    }
                  ]
                }
                """.formatted(UUID.randomUUID());

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "updated.png",
                MediaType.IMAGE_PNG_VALUE,
                "updated-image".getBytes(StandardCharsets.UTF_8)
        );

        given(clothesService.update(eq(clothesId), any(ClothesUpdateRequest.class), any(MultipartFile.class)))
                .willReturn(null);

        mockMvc.perform(
                        multipart("/api/clothes/{clothesId}", clothesId)
                                .file(requestPart)
                                .file(imagePart)
                                .with(req -> {
                                    req.setMethod("PATCH");
                                    return req;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk());

        verify(clothesService).update(eq(clothesId), any(ClothesUpdateRequest.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("의상 삭제 성공")
    void delete_success() throws Exception {
        UUID clothesId = UUID.randomUUID();

        mockMvc.perform(delete("/api/clothes/{clothesId}", clothesId))
                .andExpect(status().isNoContent());

        verify(clothesService).delete(clothesId);
    }

    @Test
    @DisplayName("의상 목록 조회 성공")
    void getClothes_success() throws Exception {
        // given
        UUID ownerId = UUID.randomUUID();
        UUID idAfter = UUID.randomUUID();

        given(clothesService.search(
                eq(ownerId),
                eq("2026-04-02T00:00:00Z"),
                eq(idAfter),
                eq(ClothesType.TOP),
                eq(20)
        )).willReturn(null);

        // when & then
        mockMvc.perform(get("/api/clothes")
                        .param("ownerId", ownerId.toString())
                        .param("cursor", "2026-04-02T00:00:00Z")
                        .param("idAfter", idAfter.toString())
                        .param("typeEqual", "TOP")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(clothesService).search(
                eq(ownerId),
                eq("2026-04-02T00:00:00Z"),
                eq(idAfter),
                eq(ClothesType.TOP),
                eq(20)
        );
    }

    @Test
    @DisplayName("URL로 의상 정보 추출 성공")
    void extractionFromUrl_success() throws Exception {
        UUID ownerId = UUID.randomUUID();
        String url = "https://example.com/item/123";

        WeatherFitUserDetails userDetails = Mockito.mock(WeatherFitUserDetails.class);
        given(userDetails.getUserId()).willReturn(ownerId);

        given(clothesService.extractionFromUrl(url, ownerId))
                .willReturn(null);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, List.of());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        try {
            mockMvc.perform(get("/api/clothes/extractions")
                            .param("url", url))
                    .andExpect(status().isOk());
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(clothesService).extractionFromUrl(url, ownerId);
    }

    @Test
    @DisplayName("의상 목록 조회 실패 - ownerId 누락")
    void getClothes_fail_withoutOwnerId() throws Exception {
        mockMvc.perform(get("/api/clothes"))
                .andExpect(status().isInternalServerError());

        verify(clothesService, never()).search(any(), any(), any(), any(), any(Integer.class));
    }
}