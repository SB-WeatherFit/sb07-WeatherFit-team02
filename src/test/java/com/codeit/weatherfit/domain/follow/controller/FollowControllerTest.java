package com.codeit.weatherfit.domain.follow.controller;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.codeit.weatherfit.domain.follow.entity.FollowCreateParam;
import com.codeit.weatherfit.domain.follow.repository.FollowRepository;
import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.message.service.ProfileFixture;
import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.service.UserService;
import com.codeit.weatherfit.global.s3.S3Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FollowControllerTest {

    @Autowired
    MockMvcTester mvcTester;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    FollowRepository followRepository;
    @MockitoBean
    private S3Service s3Service;
    @MockitoBean
    private KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        given(s3Service.getUrl(any()))
                .willReturn("https://mock-s3-url.com/default-image.jpg");
    }


    @Test
    @WithMockUser
    void createFollow() throws Exception {
        UserDto userDto = userService.create(new UserCreateRequest("name", "email@gmail.com", "1234q!"));
        UserDto userDto2 = userService.create(new UserCreateRequest("name2", "email2@gmail.com", "1234q!"));
        FollowCreateRequest followCreateRequest = new FollowCreateRequest(userDto.id(), userDto2.id());

        String json = objectMapper.writeValueAsString(followCreateRequest);

        assertThat(
                mvcTester.post()
                        .with(csrf())
                        .uri("/api/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(FollowDto.class) // JSON을 DTO로 역직렬화
                .satisfies(result -> {
                    assertThat(result.followee().userId()).isEqualTo(userDto.id());
                    assertThat(result.follower().userId()).isEqualTo(userDto2.id());
                });
    }

    @Test
    void getFollowSummary() {
        User user = UserFixture.createUser("test@email.com");
        userRepository.save(user);
        Profile profile = ProfileFixture.createProfile(user);
        profileRepository.save(profile);
        for (int i = 0; i < 10; i++) {
            User userI = UserFixture.createUser("test@email.com" + i);
            userRepository.save(userI);
            Profile profileI = ProfileFixture.createProfile(userI);
            profileRepository.save(profileI);
            Follow follow = Follow.create(new FollowCreateParam(user, userI));
            followRepository.save(follow);
        }
        User userMy = UserFixture.createUser("testMy@email.com");
        userRepository.save(userMy);
        Profile profileMy = ProfileFixture.createProfile(userMy);
        profileRepository.save(profileMy);
        Follow follow = Follow.create(new FollowCreateParam(user, userMy));
        followRepository.save(follow);

        WeatherFitUserDetails details = WeatherFitUserDetails.from(userMy);


        assertThat(
                mvcTester.get()
                        .uri("/api/follows/summary")
                        .with(csrf())
                        .param("userId", user.getId().toString())
                        .with(user(details))
        )
                .hasStatusOk()
                .bodyJson()
                .convertTo(FollowSummaryDto.class)
                .satisfies(result -> {
                    assertThat(result.followeeId()).isEqualTo(user.getId());
                    assertThat(result.followedByMe()).isTrue();
                    assertThat(result.followingMe()).isFalse();
                    assertThat(result.followerCount()).isEqualTo(11);
                });
    }

    @Test
    @WithMockUser
    void getFollowees() {
        User user = UserFixture.createUser("test@email.com");
        userRepository.save(user);
        Profile profile = ProfileFixture.createProfile(user);
        profileRepository.save(profile);
        for (int i = 0; i < 10; i++) {
            User userI = UserFixture.createUser("test@email.com" + i);
            userRepository.save(userI);
            Profile profileI = ProfileFixture.createProfile(userI);
            profileRepository.save(profileI);
            Follow follow = Follow.create(new FollowCreateParam(userI, user));
            followRepository.save(follow);
        }

        FolloweeSearchCondition followeeSearchCondition = new FolloweeSearchCondition(user.getId(), null, null, 20, null);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> map = objectMapper.convertValue(followeeSearchCondition, new TypeReference<>() {});
        params.setAll(map);

        assertThat(
                mvcTester.get().uri("/api/follows/followings")
                        .with(csrf())
                        .queryParams(params)
        )
                .hasStatusOk()
                .bodyJson()
                .convertTo(FollowListResponse.class)
                .satisfies(result -> {
                    assertThat(result.data().size()).isEqualTo(10);
                    assertThat(result.hasNext()).isFalse();
                    assertThat(result.totalCount()).isEqualTo(10);
                });
    }

    @Test
    @WithMockUser
    void getFollowers() {
        User user = UserFixture.createUser("test@email.com");
        userRepository.save(user);
        Profile profile = ProfileFixture.createProfile(user);
        profileRepository.save(profile);
        for (int i = 0; i < 10; i++) {
            User userI = UserFixture.createUser("test@email.com" + i);
            userRepository.save(userI);
            Profile profileI = ProfileFixture.createProfile(userI);
            profileRepository.save(profileI);
            Follow follow = Follow.create(new FollowCreateParam(user, userI));
            followRepository.save(follow);
        }

        FollowerSearchCondition followeeSearchCondition = new FollowerSearchCondition(user.getId(), null, null, 20, null);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> map = objectMapper.convertValue(followeeSearchCondition, new TypeReference<>() {
        });
        params.setAll(map);

        assertThat(
                mvcTester.get().uri("/api/follows/followers")
                        .with(csrf())
                        .queryParams(params)
        )
                .hasStatusOk()
                .bodyJson()
                .convertTo(FollowListResponse.class)
                .satisfies(result -> {
                    assertThat(result.data().size()).isEqualTo(10);
                    assertThat(result.hasNext()).isFalse();
                    assertThat(result.totalCount()).isEqualTo(10);
                });
    }

    @Test
    @WithMockUser
    void unFollow() {
        User user = UserFixture.createUser("test@email.com");
        userRepository.save(user);
        Profile profile = ProfileFixture.createProfile(user);
        profileRepository.save(profile);

        User user2 = UserFixture.createUser("test2@email.com");
        userRepository.save(user2);
        Profile profileI = ProfileFixture.createProfile(user2);
        profileRepository.save(profileI);
        Follow follow = Follow.create(new FollowCreateParam(user, user2));
        Follow save = followRepository.save(follow);


        assertThat(
                mvcTester.delete()
                        .uri("/api/follows/{followId}", save.getId())
                        .with(csrf())
        )

                .hasStatus(HttpStatus.NO_CONTENT);
    }
}