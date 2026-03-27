package com.codeit.weatherfit.domain.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FollowControllerTest {

    @Autowired
    MockMvcTester mvcTester;
    ObjectMapper objectMapper = new ObjectMapper();
//
//
//    @Test
//    void createFollow() throws Exception {
//        FollowCreateRequest followCreateRequest = new FollowCreateRequest(UUID.randomUUID(), UUID.randomUUID());
//        UUID followId = UUID.randomUUID();
//        FollowUser followUser = new FollowUser(UUID.randomUUID(), "test", "profileImageUrl");
//
//
//        String json = objectMapper.writeValueAsString(followCreateRequest);
//
//        assertThat(
//                mvcTester.post()
//                        .uri("/api/follows")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .hasStatus(HttpStatus.CREATED)
//                .bodyJson()
//                .extractingPath("$.id").asString().isEqualTo(followId.toString());
//
//    }
//
//    @Test
//    void getFollowSummary() {
//        UUID followeeId = UUID.randomUUID();
//
//
//        UUID userId = UUID.randomUUID();
//
//        var response = assertThat(
//                mvcTester.get()
//                        .uri("/api/follows/summary")
//                        .param("userId", userId.toString()))
//                .hasStatusOk()
//                .bodyJson();
//
////        response.extractingPath("$.followeeId").asString().isEqualTo(followeeId.toString());
//        response.extractingPath("$.followerCount").isEqualTo(5);
//        response.extractingPath("$.followingCount").isEqualTo(5);
//        response.extractingPath("$.followedByMe").isEqualTo(false);
//        response.extractingPath("$.followedByMeId").isNull();
//        response.extractingPath("$.followingMe").isEqualTo(false);
//
//    }
//
//    @Test
//    void getFollowees() {
//        var requestBuilder = mvcTester.get().uri("/api/follows/followings");
//
//        assertThat(requestBuilder)
//                .hasStatusOk();
//
//    }
//
//    @Test
//    void getFollowers() {
//
//
//        var requestBuilder = mvcTester.get().uri("/api/follows/followers");
//
//
//        assertThat(requestBuilder)
//                .hasStatusOk();
//
//    }
//
//    @Test
//    void unFollow() {
//        assertThat(
//                mvcTester.delete()
//                        .uri("/api/follows/{followId}", UUID.randomUUID()))
//                .hasStatus(HttpStatus.NO_CONTENT);
//    }
}