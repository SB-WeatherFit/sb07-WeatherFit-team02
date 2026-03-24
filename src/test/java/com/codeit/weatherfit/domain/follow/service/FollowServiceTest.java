package com.codeit.weatherfit.domain.follow.service;


import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.codeit.weatherfit.domain.follow.entity.FollowCreateParam;
import com.codeit.weatherfit.domain.follow.exception.FollowUserNotExistException;
import com.codeit.weatherfit.domain.follow.repository.FollowRepository;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class FollowServiceTest {

    @Autowired
    FollowService followService;
    @Autowired
    FollowRepository followRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    EntityManager em;

    @Test
    void follow() {
        User user = User.create("test@gmail.com", "nickname", UserRole.USER, "password");
        User user2 = User.create("test2@gmail.com", "nickname2", UserRole.USER, "password");
        User saved = userRepository.save(user);
        User saved2 = userRepository.save(user2);
        Profile profile = ProfileFixture.createProfile(saved);
        Profile profile2 = ProfileFixture.createProfile(saved2);
        profileRepository.save(profile);
        profileRepository.save(profile2);


        FollowCreateRequest followCreateRequest = new FollowCreateRequest(saved.getId(), saved2.getId());

        em.flush();
        em.clear();

        FollowDto followDto = followService.follow(followCreateRequest);

        //profileRepository 후에...
        assertThat(followDto.follower().userId()).isEqualTo(saved2.getId());
        assertThat(followDto.followee().userId()).isEqualTo(saved.getId());
        assertThat(followDto.follower().name()).isEqualTo(saved2.getName());
        assertThat(followDto.followee().name()).isEqualTo(saved.getName());
    }

    @Test
    void summary() {
        User user = User.create("test@gmail.com", "nickname", UserRole.USER, "password");
        User user2 = User.create("test2@gmail.com", "nickname2", UserRole.USER, "password");
        User user3 = User.create("test3@gmail.com", "nickname2", UserRole.USER, "password");
        User saved = userRepository.save(user);
        User saved2 = userRepository.save(user2);
        User saved3 = userRepository.save(user3);
        Profile profile = ProfileFixture.createProfile(saved);
        Profile profile2 = ProfileFixture.createProfile(saved2);
        Profile profile3 = ProfileFixture.createProfile(saved3);
        profileRepository.save(profile);
        profileRepository.save(profile2);
        profileRepository.save(profile3);

        em.flush();
        em.clear();

        FollowCreateRequest followCreateRequest = new FollowCreateRequest(saved.getId(), saved2.getId());
        FollowCreateRequest followCreateRequest2 = new FollowCreateRequest(saved.getId(), saved3.getId());

        FollowDto followDto = followService.follow(followCreateRequest);
        FollowDto followDto2 = followService.follow(followCreateRequest2);

        em.flush();
        em.clear();

        FollowSummaryDto followSummary = followService.getFollowSummary(saved.getId(), saved2.getId());

        assertThat(followSummary.followeeId()).isEqualTo(saved.getId());
        assertThat(followSummary.followedByMe()).isTrue();
        assertThat(followSummary.followedByMeId()).isEqualTo(followDto.id());
        assertThat(followSummary.followingMe()).isFalse();
        assertThat(followSummary.followerCount()).isEqualTo(2);
        assertThat(followSummary.followingCount()).isEqualTo(0);
    }

    @Test
    void summaryFail() {
        assertThatThrownBy(()-> followService.getFollowSummary(UUID.randomUUID(), UUID.randomUUID()))
        .isInstanceOf(FollowUserNotExistException.class);
    }

    @Test
    void unFollow() {
        User user = User.create("test@gmail.com", "nickname", UserRole.USER, "password");
        User user2 = User.create("test2@gmail.com", "nickname2", UserRole.USER, "password");
        User saved = userRepository.save(user);
        User saved2 = userRepository.save(user2);

        Follow savedFollow = followRepository.save(Follow.create(new FollowCreateParam(saved, saved2)));

        em.flush();
        em.clear();

        followService.unFollow(savedFollow.getId());

        boolean result = followRepository.existsById(savedFollow.getId());
        assertThat(result).isFalse();
    }
    
    @Test
    void getFollowers() {
        User user = User.create("test@gmail.com", "nickname2", UserRole.USER, "password");
        User saved = userRepository.save(user);
        Profile profile = ProfileFixture.createProfile(saved);
        profileRepository.save(profile);
        for (int i = 0; i < 50; i++) {
            User userI = User.create("test@gmail.com"+i, "nickname", UserRole.USER, "password");
            User savedI = userRepository.save(userI);
            Profile profileI = ProfileFixture.createProfile(savedI);
            profileRepository.save(profileI);
           
            followRepository.save(Follow.create(new FollowCreateParam(saved, savedI)));
        }
        
        FollowerSearchCondition condition = new FollowerSearchCondition(saved.getId(), null, null, 20, null);
        FollowListResponse result = followService.getFollowers(condition);
        
        assertThat(result.data().getFirst().followee().userId()).isEqualTo(saved.getId());
        assertThat(result.data().getFirst().follower().userId()).isNotEqualTo(saved.getId());
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotNull();
        assertThat(result.nextIdAfter()).isNotNull();
        assertThat(result.totalCount()).isEqualTo(50);
    }
}