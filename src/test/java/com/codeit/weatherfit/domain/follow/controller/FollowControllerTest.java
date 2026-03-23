package com.codeit.weatherfit.domain.follow.controller;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowUser;
import com.codeit.weatherfit.domain.follow.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FollowControllerTest {

    private FollowService followService;

    private MockMvcTester mvcTester;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        followService = Mockito.mock(FollowService.class);

        FollowController followController = new FollowController(followService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .build();

        mvcTester = MockMvcTester.create(mockMvc);
    }

    @Test
    void createFollow() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id").asString().isEqualTo(followId.toString());

        verify(followService).follow(any());
    }

    @Test
    void getFollowSummary() {
        UUID followeeId = UUID.randomUUID();
        FollowSummaryDto followSummaryDto = new FollowSummaryDto(followeeId, 5, 5, false, null, false);

        when(followService.getFollowSummary(any(UUID.class), any(UUID.class)))
                .thenReturn(followSummaryDto);

        UUID userId = UUID.randomUUID();

        var response = assertThat(
                mvcTester.get()
                        .uri("/api/follows/summary")
                        .param("userId", userId.toString()))
                .hasStatusOk()
                .bodyJson();

        response.extractingPath("$.followeeId").asString().isEqualTo(followeeId.toString());
        response.extractingPath("$.followerCount").isEqualTo(5);
        response.extractingPath("$.followeeCount").isEqualTo(5);
        response.extractingPath("$.followedByMe").isEqualTo(false);
        response.extractingPath("$.followedByMeId").isNull();
        response.extractingPath("$.followingMe").isEqualTo(false);

        verify(followService).getFollowSummary(any(UUID.class), any(UUID.class));
    }

    @Test
    void getFollowees() {
        FolloweeSearchCondition condition = new FolloweeSearchCondition(UUID.randomUUID(), null, null, 20, null);
        FollowListResponse response = new FollowListResponse(
                List.of(),
                Instant.now(),
                UUID.randomUUID(),
                false,
                0L
        );

        when(followService.getFollowees(any(FolloweeSearchCondition.class)))
                .thenReturn(response);

        Map<String, String> params = objectMapper.convertValue(condition, new TypeReference<>() {
        });

        var requestBuilder = mvcTester.get().uri("/api/follows/followings");
        params.forEach(requestBuilder::param);

        assertThat(requestBuilder)
                .hasStatusOk();

        verify(followService).getFollowees(any(FolloweeSearchCondition.class));
    }

    @Test
    void getFollowers() {
        FollowerSearchCondition condition = new FollowerSearchCondition(UUID.randomUUID(), null, null, 20, null);
        FollowListResponse response = new FollowListResponse(
                List.of(),
                Instant.now(),
                UUID.randomUUID(),
                false,
                0L
        );

        when(followService.getFollowers(any(FollowerSearchCondition.class)))
                .thenReturn(response);

        Map<String, String> params = objectMapper.convertValue(condition, new TypeReference<>() {
        });

        var requestBuilder = mvcTester.get().uri("/api/follows/followers");
        params.forEach(requestBuilder::param);

        assertThat(requestBuilder)
                .hasStatusOk();

        verify(followService).getFollowers(any(FollowerSearchCondition.class));
    }

    @Test
    void unFollow() {
        assertThat(
                mvcTester.delete()
                        .uri("/api/follows/{followId}", UUID.randomUUID()))
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(followService).unFollow(any(UUID.class));
    }
}