package com.codeit.weatherfit.domain.follow.controller;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowUser;
import com.codeit.weatherfit.domain.follow.service.FollowService;
import com.codeit.weatherfit.global.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(FollowController.class)
@Import(SecurityConfig.class)
class FollowControllerTest {
    @MockitoBean
    FollowService followService;

    @Autowired
    MockMvcTester mvcTester;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createFollow() {
        FollowCreateRequest followCreateRequest = new FollowCreateRequest(UUID.randomUUID(), UUID.randomUUID());
        UUID followId = UUID.randomUUID();
        FollowUser followUser = new FollowUser(UUID.randomUUID(), "test", "profileImageUrl");
        FollowDto followDto = new FollowDto(followId, followUser, followUser);
        when(followService.follow(any()))
                .thenReturn(followDto);
        String json = objectMapper.writeValueAsString(followCreateRequest);

        assertThat(
                mvcTester.post()
                        .uri("/api/follows")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .satisfies(result -> {
                    System.out.println("### Response Body: " + result.getResponse().getContentAsString());
                })
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id").asString().isEqualTo(followId.toString());

        verify(followService).follow(any());
    }

    @Test
    void getFollowSummary() {
        FollowSummaryDto followSummaryDto = new FollowSummaryDto(UUID.randomUUID(), 5, 5, false, null, false);
        when(followService.getFollowSummary(any(UUID.class), any(UUID.class)))
                .thenReturn(followSummaryDto);

        UUID userId = UUID.randomUUID();
        assertThat(
                mvcTester.get().uri("/api/follows/summary")
                        .param("useId", userId.toString())

        )
                .satisfies(result -> {
                    System.out.println("Response Body: " + result.getResponse().getContentAsString());
                })
                .hasStatusOk()
                .bodyJson()
                .convertTo(FollowSummaryDto.class)
                .satisfies(response -> {
                    // 이제 자바 객체이므로 Getter로 접근 가능 (Type-safe)
                    assertThat(response.followingMe()).isFalse();
                    assertThat(response.followeeId()).isNotNull();
                    assertThat(response.followedByMe()).isFalse();
                    assertThat(response.followerCount()).isEqualTo(5);
                    assertThat(response.followeeCount()).isEqualTo(5);
                    assertThat(response.followedByMeId()).isNull();
                });

        verify(followService).getFollowSummary(any(UUID.class), any(UUID.class));
    }

    @Test
    void getFollowees() {
        FolloweeSearchCondition condition = new FolloweeSearchCondition(UUID.randomUUID(), null, null, 20, null);

        Map<String, String> params = objectMapper.convertValue(condition, new TypeReference<>() {
        });
        var requestBuilder = mvcTester.get().uri("/api/follows/followings");
        params.forEach(requestBuilder::param);

        assertThat(requestBuilder)
                .hasStatusOk();

        verify(followService).getFollowees(any());
    }

    @Test
    void getFollowers() {
        FollowerSearchCondition condition = new FollowerSearchCondition(UUID.randomUUID(), null, null, 20, null);

        Map<String, String> params = objectMapper.convertValue(condition, new TypeReference<>() {
        });
        var requestBuilder = mvcTester.get().uri("/api/follows/followers");
        params.forEach(requestBuilder::param);

        assertThat(requestBuilder)
                .hasStatusOk();

        verify(followService).getFollowers(any());
    }

    @Test
    void unFollow() {
        assertThat(
                mvcTester.delete()
                        .uri("/api/follows/{followId}", UUID.randomUUID())
                        .with(csrf())
        )
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(followService).unFollow(any());
    }
}