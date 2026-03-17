package com.codeit.weatherfit.domain.follow.repository;

import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.codeit.weatherfit.domain.follow.entity.FollowCreateParam;
import com.codeit.weatherfit.domain.follow.entity.FollowFixture;
import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.config.JpaAuditingConfig;
import com.codeit.weatherfit.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
class FollowCustomRepositoryTest {

    @Autowired
    FollowRepository followRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    void searchFollowees() {
        User user = UserFixture.create();
        User saved = userRepository.save(user);
        for (int i = 0; i < 50; i++) {
            User userI = UserFixture.create("test@gmail.com" + i);
            userRepository.save(userI);
            Follow follow = Follow.create(new FollowCreateParam(userI, user));
            followRepository.save(follow);
            ReflectionTestUtils.setField(follow, "createdAt", Instant.now().minusSeconds(i));
        }

        em.flush();
        em.clear();

        FolloweeSearchCondition condition = new FolloweeSearchCondition(saved.getId(), null, null, 20, null);
        List<Follow> follows = followRepository.searchFollowees(condition);

        assertThat(follows.size()).isEqualTo(21);
        assertThat(follows).allSatisfy(f ->
                assertThat(f.getFollower().getId()).isEqualTo(saved.getId())
        );


        List<Follow> follows2 = followRepository.searchFollowees(new FolloweeSearchCondition(saved.getId(), follows.get(19).getCreatedAt(), null, 20, null));

        assertThat(follows2.size()).isEqualTo(21);
        assertThat(follows2).allSatisfy(f ->
                assertThat(f.getFollower().getId()).isEqualTo(saved.getId())
        );

        List<Follow> follows3 = followRepository.searchFollowees(new FolloweeSearchCondition(saved.getId(), follows2.get(19).getCreatedAt(), null, 20, null));

        assertThat(follows3.size()).isEqualTo(10);
        assertThat(follows3).allSatisfy(f ->
                assertThat(f.getFollower().getId()).isEqualTo(saved.getId())
        );
    }

    @Test
    void searchFollowers() {
        User user = UserFixture.create();
        User saved = userRepository.save(user);
        for (int i = 0; i < 50; i++) {
            User userI = UserFixture.create("test@gmail.com" + i);
            userRepository.save(userI);
            Follow follow = Follow.create(new FollowCreateParam(user, userI));
            followRepository.save(follow);
            ReflectionTestUtils.setField(follow, "createdAt", Instant.now().minusSeconds(i));
        }

        em.flush();
        em.clear();

        List<Follow> follows = followRepository.searchFollowers(new FollowerSearchCondition(saved.getId(), null, null, 20, null));

        assertThat(follows.size()).isEqualTo(21);
        assertThat(follows).allSatisfy(f ->
                assertThat(f.getFollowee().getId()).isEqualTo(saved.getId())
        );


        List<Follow> follows2 = followRepository.searchFollowers(new FollowerSearchCondition(saved.getId(), follows.get(19).getCreatedAt(), null, 20, null));

        assertThat(follows2.size()).isEqualTo(21);
        assertThat(follows2).allSatisfy(f ->
                assertThat(f.getFollowee().getId()).isEqualTo(saved.getId())
        );

        List<Follow> follows3 = followRepository.searchFollowers(new FollowerSearchCondition(saved.getId(), follows2.get(19).getCreatedAt(), null, 20, null));

        assertThat(follows3.size()).isEqualTo(10);
        assertThat(follows3).allSatisfy(f ->
                assertThat(f.getFollowee().getId()).isEqualTo(saved.getId())
        );
    }
}