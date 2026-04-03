package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        ClothesRepositoryImpl.class,
        ClothesRepositoryImplTest.QuerydslConfig.class
})
class ClothesRepositoryImplTest {

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private ClothesRepositoryImpl clothesRepositoryImpl;

    @Autowired
    private EntityManager em;

    @TestConfiguration
    static class QuerydslConfig {

        @PersistenceContext
        private EntityManager em;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(em);
        }
    }

    @Test
    @DisplayName("count: 타입 없이 전체 개수")
    void count_withoutType() {
        User user = createUser();

        saveClothes(user, ClothesType.TOP, nowMinus(1));
        saveClothes(user, ClothesType.BOTTOM, nowMinus(2));

        long result = clothesRepositoryImpl.count(user.getId(), null);

        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("count: 타입 필터 적용")
    void count_withType() {
        User user = createUser();

        saveClothes(user, ClothesType.TOP, nowMinus(1));
        saveClothes(user, ClothesType.BOTTOM, nowMinus(2));

        long result = clothesRepositoryImpl.count(user.getId(), ClothesType.TOP);

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("search: 첫 페이지 조회")
    void search_firstPage() {
        User user = createUser();

        saveClothes(user, ClothesType.TOP, nowMinus(1));
        saveClothes(user, ClothesType.TOP, nowMinus(2));
        saveClothes(user, ClothesType.TOP, nowMinus(3));

        List<Clothes> result = clothesRepositoryImpl.search(
                user.getId(),
                null,
                null,
                ClothesType.TOP,
                2
        );

        assertThat(result).hasSize(3); // size + 1
        assertThat(result.get(0).getCreatedAt())
                .isAfter(result.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("search: 커서 기반 다음 페이지")
    void search_nextPage() {
        User user = createUser();

        Clothes c1 = saveClothes(user, ClothesType.TOP, nowMinus(1));
        Clothes c2 = saveClothes(user, ClothesType.TOP, nowMinus(2));
        Clothes c3 = saveClothes(user, ClothesType.TOP, nowMinus(3));

        List<Clothes> result = clothesRepositoryImpl.search(
                user.getId(),
                c2.getCreatedAt(),
                c2.getId(),
                ClothesType.TOP,
                2
        );

        assertThat(result).isNotNull();
    }

    private User createUser() {
        User user = User.create(
                "test@test.com",
                "1234",
                UserRole.USER,
                "nickname"
        );

        ReflectionTestUtils.setField(user, "createdAt", Instant.now());

        em.persist(user);
        return user;
    }

    private Clothes saveClothes(User user, ClothesType type, Instant createdAt) {
        Clothes clothes = Clothes.create(
                user,
                "test",
                type,
                "image-key"
        );

        ReflectionTestUtils.setField(clothes, "createdAt", createdAt);

        em.persist(clothes);
        return clothes;
    }

    private Instant nowMinus(int seconds) {
        return Instant.now().minusSeconds(seconds);
    }
}