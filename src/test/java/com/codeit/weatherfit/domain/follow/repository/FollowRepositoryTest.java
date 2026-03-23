package com.codeit.weatherfit.domain.follow.repository;

import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.codeit.weatherfit.domain.follow.entity.FollowCreateParam;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
@Transactional
class FollowRepositoryTest {

    @Autowired
    FollowRepository followRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    void existsByFolloweeIdAndFollowerId() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);
        User user2 = UserFixture.createUser("test2@gmail.com");
        User saved2 = userRepository.save(user2);
        Follow follow = Follow.create(new FollowCreateParam(user2, user));
        followRepository.save(follow);

        em.flush();
        em.clear();

        boolean result = followRepository.existsByFolloweeIdAndFollowerId(saved2.getId(), saved.getId());

        assertThat(result).isTrue();
    }

    @Test
    void getFollowingCount() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);
        for (int i = 0; i < 50; i++) {
            User userI = UserFixture.createUser("test@gmail.com" + i);
            userRepository.save(userI);
            Follow follow = Follow.create(new FollowCreateParam(userI, saved));
            followRepository.save(follow);
            ReflectionTestUtils.setField(follow, "createdAt", Instant.now().minusSeconds(i));
        }

        em.flush();
        em.clear();

        long result = followRepository.getFollowingCount(saved.getId());

        assertThat(result).isEqualTo(50);
    }

    @Test
    void getFollowerCount() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);
        for (int i = 0; i < 50; i++) {
            User userI = UserFixture.createUser("test@gmail.com" + i);
            User savedI = userRepository.save(userI);
            Follow follow = Follow.create(new FollowCreateParam(saved, savedI));
            followRepository.save(follow);
            ReflectionTestUtils.setField(follow, "createdAt", Instant.now().minusSeconds(i));
        }

        em.flush();
        em.clear();

        long result = followRepository.getFollowerCount(saved.getId());
        long result2 = followRepository.getFollowingCount(saved.getId());

        assertThat(result).isEqualTo(50);
        assertThat(result2).isEqualTo(0);
    }

    @Test
    void findByFolloweeIdAndFollowerId() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);
        User userI = UserFixture.createUser("test@gmail.com2");
        User savedI = userRepository.save(userI);
        Follow follow = Follow.create(new FollowCreateParam(saved, savedI));
        Follow savedFollow = followRepository.save(follow);

        em.flush();
        em.clear();

        Follow found = followRepository.findByFolloweeIdAndFollowerId(saved.getId(), savedI.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(savedFollow.getId());
        assertThat(found).isEqualTo(savedFollow);
    }

    @Test
    void countByFollowerId() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);
        for (int i = 0; i < 50; i++) {
            User userI = UserFixture.createUser("test@gmail.com" + i);
            User savedI = userRepository.save(userI);
            Follow follow = Follow.create(new FollowCreateParam(saved, savedI));
            followRepository.save(follow);
            ReflectionTestUtils.setField(follow, "createdAt", Instant.now().minusSeconds(i));
        }

        em.flush();
        em.clear();

        long result = followRepository.countByFollowerId(saved.getId());
        long result2 = followRepository.countByFolloweeId(saved.getId());

        assertThat(result).isEqualTo(0);
        assertThat(result2).isEqualTo(50);
    }
}