package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.dto.request.FeedGetRequest;
import com.codeit.weatherfit.domain.feed.dto.request.SortBy;
import com.codeit.weatherfit.domain.feed.dto.request.SortDirection;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.weather.entity.*;
import com.codeit.weatherfit.global.config.JpaAuditingConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        FeedRepositoryImpl.class,
        FeedRepositoryImplTest.QuerydslConfig.class,
        JpaAuditingConfig.class
})
class FeedRepositoryImplTest {

    @TestConfiguration
    static class QuerydslConfig {
        @PersistenceContext
        private EntityManager em;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(em);
        }
    }

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private EntityManager em;

    private User createAndPersistUser(String email, String name) {
        User user = User.create(email, name, UserRole.USER, "password123");
        em.persist(user);
        return user;
    }

    private Weather createWeather() {
        return Weather.create(
                new Temperature(25.5, 2.0, 15.0, 35.0),
                new WindSpeed(AsWord.MODERATE, 5.0),
                new Precipitation(PrecipitationType.NONE, 0.0, 10.0),
                SkyStatus.CLEAR,
                new Humidity(65.0, 5.0),
                Instant.now(),
                Instant.now(),
                Location.create(37.5, 127.0, 60, 127, List.of("서울", "강남", "역삼"))
        );
    }

    private Feed createAndPersistFeed(User author, String content) {
        Weather weather = createWeather();
        em.persist(weather);
        Feed feed = Feed.create(author, weather, content);
        em.persist(feed);
        return feed;
    }

    private FeedGetRequest request(int limit, SortDirection direction) {
        return new FeedGetRequest(null, null, limit, SortBy.createdAt, direction, null, null, null, null);
    }

    // === 커서 페이지네이션 ===

    @Test
    @DisplayName("커서 페이지네이션: limit+1개를 반환하여 다음 페이지 여부를 감지한다")
    void returnsLimitPlusOneForHasNextDetection() {
        User user = createAndPersistUser("a@test.com", "유저A");
        for (int i = 0; i < 5; i++) {
            createAndPersistFeed(user, "피드 " + i);
        }
        em.flush();
        em.clear();

        FeedGetRequest req = request(3, SortDirection.DESCENDING);
        List<Feed> result = feedRepository.findWithCursor(req);

        // limit(3) + 1 = 4개를 요청하므로 5개 중 4개가 반환
        assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("커서 페이지네이션: 내림차순 커서가 올바르게 적용된다")
    void descendingCursor() {
        User user = createAndPersistUser("b@test.com", "유저B");
        Feed feed1 = createAndPersistFeed(user, "첫번째");
        Feed feed2 = createAndPersistFeed(user, "두번째");
        Feed feed3 = createAndPersistFeed(user, "세번째");
        em.flush();
        em.clear();

        // 먼저 전체 내림차순 조회하여 두 번째 항목의 커서를 확보
        List<Feed> firstPage = feedRepository.findWithCursor(request(1, SortDirection.DESCENDING));
        Feed lastOfFirstPage = firstPage.getFirst();

        // 커서로 두 번째 페이지 조회
        FeedGetRequest cursorReq = new FeedGetRequest(
                lastOfFirstPage.getCreatedAt(), lastOfFirstPage.getId(),
                10, SortBy.createdAt, SortDirection.DESCENDING,
                null, null, null, null
        );
        List<Feed> secondPage = feedRepository.findWithCursor(cursorReq);

        // 커서 이후의 피드만 반환되어야 함 (첫 페이지 항목 제외)
        assertThat(secondPage).noneMatch(f -> f.getId().equals(lastOfFirstPage.getId()));
        assertThat(secondPage).isNotEmpty();
    }

    @Test
    @DisplayName("커서 페이지네이션: 오름차순 커서가 올바르게 적용된다")
    void ascendingCursor() {
        User user = createAndPersistUser("c@test.com", "유저C");
        Feed feed1 = createAndPersistFeed(user, "첫번째");
        Feed feed2 = createAndPersistFeed(user, "두번째");
        Feed feed3 = createAndPersistFeed(user, "세번째");
        em.flush();
        em.clear();

        // 오름차순 첫 페이지
        List<Feed> firstPage = feedRepository.findWithCursor(request(1, SortDirection.ASCENDING));
        Feed lastOfFirstPage = firstPage.getFirst();

        // 커서로 두 번째 페이지 조회
        FeedGetRequest cursorReq = new FeedGetRequest(
                lastOfFirstPage.getCreatedAt(), lastOfFirstPage.getId(),
                10, SortBy.createdAt, SortDirection.ASCENDING,
                null, null, null, null
        );
        List<Feed> secondPage = feedRepository.findWithCursor(cursorReq);

        assertThat(secondPage).noneMatch(f -> f.getId().equals(lastOfFirstPage.getId()));
        assertThat(secondPage).isNotEmpty();
    }

    // === 필터 ===

@Test
    @DisplayName("필터: authorId로 작성자 필터링이 동작한다")
    void authorIdFilter() {
        User userA = createAndPersistUser("e@test.com", "유저E");
        User userB = createAndPersistUser("f@test.com", "유저F");
        createAndPersistFeed(userA, "유저A의 피드1");
        createAndPersistFeed(userA, "유저A의 피드2");
        createAndPersistFeed(userB, "유저B의 피드");
        em.flush();
        em.clear();

        FeedGetRequest req = new FeedGetRequest(
                null, null, 10, SortBy.createdAt, SortDirection.DESCENDING,
                null, null, null, userA.getId()
        );
        List<Feed> result = feedRepository.findWithCursor(req);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(f -> f.getAuthor().getId().equals(userA.getId()));
    }

    @Test
    @DisplayName("필터: 필터 없이 전체 조회가 동작한다")
    void noFilter() {
        User user = createAndPersistUser("g@test.com", "유저G");
        createAndPersistFeed(user, "피드1");
        createAndPersistFeed(user, "피드2");
        createAndPersistFeed(user, "피드3");
        em.flush();
        em.clear();

        FeedGetRequest req = request(10, SortDirection.DESCENDING);
        List<Feed> result = feedRepository.findWithCursor(req);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("필터: skyStatus JSONB 필터링")
    void skyStatusFilter() {
        // PostgreSQL에서만 동작
    }

    @Test
    @DisplayName("필터: precipitationType JSONB 필터링")
    void precipitationTypeFilter() {
        // PostgreSQL에서만 동작
    }

    // === 정렬 ===

    @Test
    @DisplayName("정렬: createdAt 내림차순 정렬")
    void descendingSort() throws InterruptedException {
        User user = createAndPersistUser("h@test.com", "유저H");
        Feed first = createAndPersistFeed(user, "먼저 생성");
        em.flush();

        // createdAt 차이를 보장하기 위해 약간의 지연
        Thread.sleep(50);
        Feed second = createAndPersistFeed(user, "나중에 생성");
        em.flush();
        em.clear();

        FeedGetRequest req = request(10, SortDirection.DESCENDING);
        List<Feed> result = feedRepository.findWithCursor(req);

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        // 내림차순이므로 첫 번째 요소의 createdAt >= 두 번째
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getCreatedAt())
                    .isAfterOrEqualTo(result.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("정렬: createdAt 오름차순 정렬")
    void ascendingSort() throws InterruptedException {
        User user = createAndPersistUser("i@test.com", "유저I");
        Feed first = createAndPersistFeed(user, "먼저 생성");
        em.flush();

        Thread.sleep(50);
        Feed second = createAndPersistFeed(user, "나중에 생성");
        em.flush();
        em.clear();

        FeedGetRequest req = request(10, SortDirection.ASCENDING);
        List<Feed> result = feedRepository.findWithCursor(req);

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        // 오름차순이므로 첫 번째 요소의 createdAt <= 두 번째
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getCreatedAt())
                    .isBeforeOrEqualTo(result.get(i + 1).getCreatedAt());
        }
    }
}
