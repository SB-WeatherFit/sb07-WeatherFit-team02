package com.codeit.weatherfit.domain.feed.service.unit;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;
import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import com.codeit.weatherfit.domain.feed.repository.CommentRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.service.FeedServiceImpl;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.service.UserService;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import com.codeit.weatherfit.global.s3.S3Service;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private FeedRepository feedRepository;
    @Mock
    private FeedLikeRepository feedLikeRepository;

    @Mock
    private FeedClothesRepository feedClothesRepository;

    @Mock
    private ClothesRepository clothesRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private FeedServiceImpl feedService;

    @Mock
    UserService userService;

    @Mock
    S3Service s3Service;

    @Nested
    @DisplayName("생성")
    class create {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            FeedCreateRequest request = Instancio.create(FeedCreateRequest.class);
            when(userRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(Instancio.create(User.class)));
            when(weatherRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(Instancio.create(Weather.class)));
            when(feedRepository.save(any(Feed.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            when(clothesRepository.findAllById(request.clothesIds()))
                    .thenAnswer(invocation -> Instancio.ofList(Clothes.class).size(request.clothesIds().size()).create());
            stubToFeedDto();

            // when
            feedService.create(request);

            // then
            verify(userRepository).findById(request.userId());
            verify(weatherRepository).findById(request.weatherId());
            verify(feedRepository).save(any(Feed.class));
            verify(feedClothesRepository).saveAll(anyList());
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("피드를 만든 유저는 존재해야한다.")
            void user() {
                // given
                when(userRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.create(Instancio.create(FeedCreateRequest.class)))
                        .isInstanceOf(IllegalArgumentException.class); // 추후 커스텀 에러로 수정
            }

            @Test
            @DisplayName("weather가 존재해야한다")
            void weather() {
                // given
                when(userRepository.findById(any()))
                        .thenReturn(Optional.of(Instancio.create(User.class)));
                when(weatherRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.create(Instancio.create(FeedCreateRequest.class)))
                        .isInstanceOf(WeatherNotFoundException.class);
            }

            @Test
            @DisplayName("옷이 존재해야한다")
            void clothes() {
                // given
                List<UUID> clothesIds = List.of(UUID.randomUUID(), UUID.randomUUID());
                FeedCreateRequest request = new FeedCreateRequest(
                        UUID.randomUUID(), UUID.randomUUID(), clothesIds, "내용"
                );
                when(userRepository.findById(any()))
                        .thenReturn(Optional.of(Instancio.create(User.class)));
                when(weatherRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(Instancio.create(Weather.class)));
                when(clothesRepository.findAllById(any()))
                        .thenReturn(List.of(Instancio.create(Clothes.class))); // 2개 요청, 1개만 반환

                // when & then
                assertThatThrownBy(() -> feedService.create(request))
                        .isInstanceOf(IllegalArgumentException.class); // 추후 커스텀 에러로 수정
            }

        }
    }

    @Nested
    @DisplayName("수정")
    class update {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Feed feed = Instancio.of(Feed.class)
                    .set(all(Instant.class), Instant.now().minus(3, ChronoUnit.DAYS))
                    .create();
            String oldContent = feed.getContent();
            String newContent = "새로바뀝";
            when(feedRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(feed));
            stubToFeedDto();
            FeedUpdateRequest feedUpdateRequest = new FeedUpdateRequest(newContent);

            // when
            FeedDto update = feedService.update(UUID.randomUUID(), feedUpdateRequest);

            // then
            assertThat(update.content()).isNotEqualTo(oldContent);
            assertThat(update.content()).isEqualTo(feedUpdateRequest.content());
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("존재하지 않는 피드는 수정할 수 없다.")
            void feedNotFound() {
                // given
                when(feedRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());
                FeedUpdateRequest request = new FeedUpdateRequest("내용 수정");

                // when & then
                assertThatThrownBy(() -> feedService.update(UUID.randomUUID(), request))
                        .isInstanceOf(com.codeit.weatherfit.domain.feed.exception.FeedNotExistException.class);
            }
        }

    }

    @Nested
    @DisplayName("피드 조회")
    class getFeedsByCursor {
        @Test
        @DisplayName("성공 - 다음 페이지가 있는 경우")
        void successWithNextPage() {
            // given
            int limit = 3;
            FeedGetRequest request = new FeedGetRequest(
                    null, null, limit, SortBy.createdAt, SortDirection.DESCENDING,
                    null, null, null, null
            );
            List<Feed> feeds = Instancio.ofList(Feed.class)
                    .size(limit + 1)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(feedRepository.findWithCursor(request)).thenReturn(feeds);
            stubToFeedDto();

            // when
            FeedGetResponse response = feedService.getFeedsByCursor(request);

            // then
            Feed expectedLastFeed = feeds.get(limit - 1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.data()).hasSize(limit);
            assertThat(response.nextCursor()).isEqualTo(expectedLastFeed.getCreatedAt());
            assertThat(response.nextIdAfter()).isEqualTo(expectedLastFeed.getId());
            verify(feedRepository).findWithCursor(request);
        }

        @Test
        @DisplayName("성공 - 다음 페이지가 없는 경우")
        void successWithoutNextPage() {
            // given
            int limit = 3;
            FeedGetRequest request = new FeedGetRequest(
                    null, null, limit, SortBy.createdAt, SortDirection.DESCENDING,
                    null, null, null, null
            );
            List<Feed> feeds = Instancio.ofList(Feed.class)
                    .size(limit)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(feedRepository.findWithCursor(request)).thenReturn(feeds);
            stubToFeedDto();

            // when
            FeedGetResponse response = feedService.getFeedsByCursor(request);

            // then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.data()).hasSize(limit);
            assertThat(response.nextCursor()).isNull();
            assertThat(response.nextIdAfter()).isNull();
        }

        @Test
        @DisplayName("성공 - 결과가 없는 경우")
        void successEmpty() {
            // given
            FeedGetRequest request = new FeedGetRequest(
                    null, null, 10, SortBy.createdAt, SortDirection.DESCENDING,
                    null, null, null, null
            );
            when(feedRepository.findWithCursor(request)).thenReturn(List.of());

            // when
            FeedGetResponse response = feedService.getFeedsByCursor(request);

            // then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.data()).isEmpty();
        }
    }

    @Nested
    @DisplayName("삭제")
    class delete {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Feed feed = Instancio.create(Feed.class);
            when(feedRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(feed));

            // when
            feedService.delete(feed.getId());

            // then
            verify(feedRepository).delete(feed);
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("존재하지 않는 피드는 삭제할 수 없다.")
            void feedNotFound() {
                // given
                when(feedRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.delete(UUID.randomUUID()))
                        .isInstanceOf(com.codeit.weatherfit.domain.feed.exception.FeedNotExistException.class);
            }
        }
    }

    @Nested
    @DisplayName("댓글 생성")
    class createComment {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Feed feed = Instancio.create(Feed.class);
            when(feedRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(feed));
            when(userRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(feed.getAuthor()));
            when(commentRepository.save(any()))
                    .thenReturn(Instancio.create(Comment.class));
            when(userService.getUserSummary(any(User.class)))
                    .thenReturn(Instancio.create(UserSummary.class));

            // when
            feedService.createComment(new CommentCreateRequest(
                    feed.getId(),
                    feed.getAuthor().getId(),
                    "content"
            ));

            // then
            verify(feedRepository).findById(feed.getId());
            verify(userRepository).findById(feed.getAuthor().getId());
            verify(commentRepository).save(any());
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
//            @Test // TODO : 커스텀 에러 생기면 작성
//            @DisplayName("feed Id가 존재해야한다.")
//            void feed() {
//                // given
//                when(userRepository.findById(any(UUID.class)))
//                        .thenReturn(Optional.empty());
//
//                // when
//                feedService.createComment(Instancio.create(CommentCreateRequest.class));
//
//                // then
//                assertThatThrownBy(() -> feedService.createComment(Instancio.create(CommentCreateRequest.class)))
//
//
//            }
        }
    }

    @Nested
    @DisplayName("댓글 조회")
    class getCommentsByCursor {
        @Test
        @DisplayName("성공 - 다음 페이지가 있는 경우")
        void successWithNextPage() {
            // given
            int limit = 3;
            UUID feedId = UUID.randomUUID();
            CommentGetRequest request = new CommentGetRequest(feedId, null, null, limit);

            List<Comment> comments = Instancio.ofList(Comment.class)
                    .size(limit + 1)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(commentRepository.getCommentsByCursor(request)).thenReturn(comments);
            when(userService.getUserSummary(any(User.class)))
                    .thenReturn(Instancio.create(UserSummary.class));

            // when
            CommentGetResponse response = feedService.getCommentsByCursor(request);

            // then
            Comment expectedLast = comments.get(limit - 1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.data()).hasSize(limit);
            assertThat(response.nextCursor()).isEqualTo(expectedLast.getCreatedAt());
            assertThat(response.nextIdAfter()).isEqualTo(expectedLast.getId());
        }

        @Test
        @DisplayName("성공 - 다음 페이지가 없는 경우")
        void successWithoutNextPage() {
            // given
            int limit = 3;
            UUID feedId = UUID.randomUUID();
            CommentGetRequest request = new CommentGetRequest(feedId, null, null, limit);

            List<Comment> comments = Instancio.ofList(Comment.class)
                    .size(limit)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(commentRepository.getCommentsByCursor(request)).thenReturn(comments);
            when(userService.getUserSummary(any(User.class)))
                    .thenReturn(Instancio.create(UserSummary.class));

            // when
            CommentGetResponse response = feedService.getCommentsByCursor(request);

            // then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.data()).hasSize(limit);
            assertThat(response.nextCursor()).isNull();
            assertThat(response.nextIdAfter()).isNull();
        }

        @Test
        @DisplayName("성공 - 결과가 없는 경우")
        void successEmpty() {
            // given
            UUID feedId = UUID.randomUUID();
            CommentGetRequest request = new CommentGetRequest(feedId, null, null, 10);
            when(commentRepository.getCommentsByCursor(request)).thenReturn(List.of());

            // when
            CommentGetResponse response = feedService.getCommentsByCursor(request);

            // then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.data()).isEmpty();
        }
    }

    private void stubToFeedDto() {
        when(feedClothesRepository.findAllByFeed(any(Feed.class)))
                .thenReturn(Instancio.createList(FeedClothes.class));
        when(feedLikeRepository.countByFeed(any(Feed.class)))
                .thenReturn(0L);
        when(commentRepository.countByFeed(any(Feed.class)))
                .thenReturn(0L);
        when(feedLikeRepository.existsByFeedAndUser(any(Feed.class), any(User.class)))
                .thenReturn(false);
        when(s3Service.getUrl(any()))
                .thenReturn("http://localhost:8080/image/1234567890");
    }

}