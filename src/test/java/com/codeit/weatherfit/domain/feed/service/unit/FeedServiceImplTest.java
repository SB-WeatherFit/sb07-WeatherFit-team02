package com.codeit.weatherfit.domain.feed.service.unit;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.exception.ClothesNotFoundException;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;
import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import com.codeit.weatherfit.domain.feed.entity.FeedLike;
import com.codeit.weatherfit.domain.feed.exception.*;
import com.codeit.weatherfit.domain.feed.repository.CommentRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.service.FeedServiceImpl;
import com.codeit.weatherfit.domain.follow.repository.FollowRepository;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.service.UserService;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import com.codeit.weatherfit.global.exception.WeatherFitException;
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
import java.util.*;

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
    private ClothesAttributeRepository clothesAttributeRepository;

    @Mock
    private CommentRepository commentRepository;


    @InjectMocks
    private FeedServiceImpl feedService;

    @Mock
    UserService userService;

    @Mock
    S3Service s3Service;

    @Mock
    FollowRepository followRepository;

    @Mock
    org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Mock
    com.codeit.weatherfit.domain.feed.service.search.FeedSearchService feedSearchService;


    @Nested
    @DisplayName("생성")
    class create {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User author = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);
            FeedCreateRequest request = new FeedCreateRequest(
                    author.getId(), UUID.randomUUID(), List.of(UUID.randomUUID()), "content"
            );
            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.of(author));
            when(weatherRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(Instancio.create(Weather.class)));
            when(feedRepository.save(any(Feed.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(clothesRepository.findAllById(request.clothesIds()))
                    .thenAnswer(invocation -> Instancio.ofList(Clothes.class).size(request.clothesIds().size()).create());
            when(clothesAttributeRepository.getClothesOptions(any(Clothes.class)))
                    .thenReturn(List.of("옵션1"));
            when(followRepository.findAllByFollowee(any(User.class)))
                    .thenReturn(List.of());
            stubToFeedDtos();

            // when
            feedService.create(request, userDetails);

            // then
            verify(userRepository).findById(request.authorId());
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
                User author = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);
                FeedCreateRequest request = new FeedCreateRequest(
                        author.getId(), UUID.randomUUID(), List.of(UUID.randomUUID()), "content"
                );
                when(userRepository.findById(author.getId()))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.create(request, userDetails))
                        .isInstanceOf(WeatherFitException.class);
            }

            @Test
            @DisplayName("weather가 존재해야한다")
            void weather() {
                // given
                User author = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);
                FeedCreateRequest request = new FeedCreateRequest(
                        author.getId(), UUID.randomUUID(), List.of(UUID.randomUUID()), "content"
                );
                when(userRepository.findById(author.getId()))
                        .thenReturn(Optional.of(author));
                when(weatherRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.create(request, userDetails))
                        .isInstanceOf(WeatherNotFoundException.class);
            }

            @Test
            @DisplayName("옷이 존재해야한다")
            void clothes() {
                // given
                User author = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);
                List<UUID> clothesIds = List.of(UUID.randomUUID(), UUID.randomUUID());
                FeedCreateRequest request = new FeedCreateRequest(
                        author.getId(), UUID.randomUUID(), clothesIds, "내용"
                );
                when(userRepository.findById(author.getId()))
                        .thenReturn(Optional.of(author));
                when(weatherRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(Instancio.create(Weather.class)));
                when(clothesRepository.findAllById(any()))
                        .thenReturn(List.of(Instancio.create(Clothes.class))); // 2개 요청, 1개만 반환

                // when & then
                assertThatThrownBy(() -> feedService.create(request, userDetails))
                        .isInstanceOf(ClothesNotFoundException.class);
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
            User author = feed.getAuthor();
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);
            String oldContent = feed.getContent();
            String newContent = "새로바뀝";
            when(feedRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(feed));
            stubToFeedDtos();
            FeedUpdateRequest feedUpdateRequest = new FeedUpdateRequest(newContent);

            // when
            FeedDto update = feedService.update(UUID.randomUUID(), feedUpdateRequest, userDetails);

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
                User user = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
                when(feedRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());
                FeedUpdateRequest request = new FeedUpdateRequest("내용 수정");

                // when & then
                assertThatThrownBy(() -> feedService.update(UUID.randomUUID(), request, userDetails))
                        .isInstanceOf(FeedNotExistException.class);
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
            User loginUser = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(loginUser);
            FeedGetRequest request = new FeedGetRequest(
                    null, null, limit, SortBy.createdAt, SortDirection.DESCENDING,
                    null, null, null, null
            );
            List<Feed> feeds = Instancio.ofList(Feed.class)
                    .size(limit + 1)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(feedRepository.findWithCursor(request)).thenReturn(feeds);
            when(userRepository.findById(loginUser.getId()))
                    .thenReturn(Optional.of(loginUser));
            stubToFeedDtos();

            // when
            FeedGetResponse response = feedService.getFeedsByCursor(request, userDetails);

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
            User loginUser = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(loginUser);
            FeedGetRequest request = new FeedGetRequest(
                    null, null, limit, SortBy.createdAt, SortDirection.DESCENDING,
                    null, null, null, null
            );
            List<Feed> feeds = Instancio.ofList(Feed.class)
                    .size(limit)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(feedRepository.findWithCursor(request)).thenReturn(feeds);
            when(userRepository.findById(loginUser.getId()))
                    .thenReturn(Optional.of(loginUser));
            stubToFeedDtos();

            // when
            FeedGetResponse response = feedService.getFeedsByCursor(request, userDetails);

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
            User loginUser = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(loginUser);
            FeedGetRequest request = new FeedGetRequest(
                    null, null, 10, SortBy.createdAt, SortDirection.DESCENDING,
                    null, null, null, null
            );
            when(feedRepository.findWithCursor(request)).thenReturn(List.of());
            when(userRepository.findById(loginUser.getId()))
                    .thenReturn(Optional.of(loginUser));

            // when
            FeedGetResponse response = feedService.getFeedsByCursor(request, userDetails);

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
            User author = feed.getAuthor();
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);
            when(feedRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(feed));

            // when
            feedService.delete(feed.getId(), userDetails);

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
                User user = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
                when(feedRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.delete(UUID.randomUUID(), userDetails))
                        .isInstanceOf(FeedNotExistException.class);
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
            User commenter = feed.getAuthor();
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(commenter);
            CommentCreateRequest request = new CommentCreateRequest(
                    feed.getId(),
                    commenter.getId(),
                    "content"
            );
            when(feedRepository.findById(feed.getId()))
                    .thenReturn(Optional.of(feed));
            when(userRepository.findById(commenter.getId()))
                    .thenReturn(Optional.of(commenter));
            when(commentRepository.save(any()))
                    .thenReturn(Instancio.create(Comment.class));
            when(userService.getUserSummary(any(User.class)))
                    .thenReturn(Instancio.create(UserSummary.class));

            // when
            feedService.createComment(feed.getId(), request, userDetails);

            // then
            verify(feedRepository).findById(feed.getId());
            verify(userRepository).findById(commenter.getId());
            verify(commentRepository).save(any());
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("path의 feedId와 request의 feedId가 다르면 실패한다.")
            void feedIdMismatch() {
                // given
                User commenter = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(commenter);
                UUID feedId = UUID.randomUUID();
                UUID differentFeedId = UUID.randomUUID();
                CommentCreateRequest request = new CommentCreateRequest(
                        feedId, commenter.getId(), "content"
                );

                // when & then
                assertThatThrownBy(() -> feedService.createComment(differentFeedId, request, userDetails))
                        .isInstanceOf(FeedBadRequestException.class);
            }

            @Test
            @DisplayName("로그인 유저와 요청 authorId가 다르면 실패한다.")
            void authorIdMismatch() {
                // given
                User commenter = Instancio.create(User.class);
                User otherUser = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(otherUser);
                UUID feedId = UUID.randomUUID();
                CommentCreateRequest request = new CommentCreateRequest(
                        feedId, commenter.getId(), "content"
                );

                // when & then
                assertThatThrownBy(() -> feedService.createComment(feedId, request, userDetails))
                        .isInstanceOf(FeedForbiddenException.class);
            }
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
            User user = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
            CommentGetRequest request = new CommentGetRequest(feedId, null, null, limit);

            List<Comment> comments = Instancio.ofList(Comment.class)
                    .size(limit + 1)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(commentRepository.getCommentsByCursor(request)).thenReturn(comments);
            when(userService.getUserSummary(any(User.class)))
                    .thenReturn(Instancio.create(UserSummary.class));

            // when
            CommentGetResponse response = feedService.getCommentsByCursor(request, userDetails);

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
            User user = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
            CommentGetRequest request = new CommentGetRequest(feedId, null, null, limit);

            List<Comment> comments = Instancio.ofList(Comment.class)
                    .size(limit)
                    .set(all(Instant.class), Instant.now().minus(1, ChronoUnit.DAYS))
                    .create();
            when(commentRepository.getCommentsByCursor(request)).thenReturn(comments);
            when(userService.getUserSummary(any(User.class)))
                    .thenReturn(Instancio.create(UserSummary.class));

            // when
            CommentGetResponse response = feedService.getCommentsByCursor(request, userDetails);

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
            User user = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
            CommentGetRequest request = new CommentGetRequest(feedId, null, null, 10);
            when(commentRepository.getCommentsByCursor(request)).thenReturn(List.of());

            // when
            CommentGetResponse response = feedService.getCommentsByCursor(request, userDetails);

            // then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.data()).isEmpty();
        }
    }

    @Nested
    @DisplayName("좋아요")
    class like {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Feed feed = Instancio.create(Feed.class);
            User author = feed.getAuthor();
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);

            when(feedRepository.findById(feed.getId()))
                    .thenReturn(Optional.of(feed));
            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.of(author));
            when(feedLikeRepository.existsByFeedAndLikedUser(feed, author))
                    .thenReturn(false);

            // when
            feedService.like(feed.getId(), userDetails);

            // then
            verify(feedLikeRepository).save(any(FeedLike.class));
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("존재하지 않는 피드에 좋아요할 수 없다.")
            void feedNotFound() {
                // given
                User user = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
                when(feedRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.like(UUID.randomUUID(), userDetails))
                        .isInstanceOf(FeedNotExistException.class);
            }

            @Test
            @DisplayName("존재하지 않는 유저는 좋아요할 수 없다.")
            void userNotFound() {
                // given
                Feed feed = Instancio.create(Feed.class);
                User otherUser = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(otherUser);
                when(feedRepository.findById(feed.getId()))
                        .thenReturn(Optional.of(feed));
                when(userRepository.findById(otherUser.getId()))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.like(feed.getId(), userDetails))
                        .isInstanceOf(WeatherFitException.class);
            }

            @Test
            @DisplayName("이미 좋아요한 피드에 다시 좋아요할 수 없다.")
            void alreadyLiked() {
                // given
                Feed feed = Instancio.create(Feed.class);
                User author = feed.getAuthor();
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);

                when(feedRepository.findById(feed.getId()))
                        .thenReturn(Optional.of(feed));
                when(userRepository.findById(author.getId()))
                        .thenReturn(Optional.of(author));
                when(feedLikeRepository.existsByFeedAndLikedUser(feed, author))
                        .thenReturn(true);

                // when & then
                assertThatThrownBy(() -> feedService.like(feed.getId(), userDetails))
                        .isInstanceOf(FeedLikeAlreadyExistException.class);
            }
        }
    }

    @Nested
    @DisplayName("좋아요 취소")
    class unlike {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Feed feed = Instancio.create(Feed.class);
            User author = feed.getAuthor();
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);

            when(feedRepository.findById(feed.getId()))
                    .thenReturn(Optional.of(feed));
            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.of(author));
            when(feedLikeRepository.existsByFeedAndLikedUser(feed, author))
                    .thenReturn(true);

            // when
            feedService.unlike(feed.getId(), userDetails);

            // then
            verify(feedLikeRepository).existsByFeedAndLikedUser(feed, author);
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("존재하지 않는 피드의 좋아요는 취소할 수 없다.")
            void feedNotFound() {
                // given
                User user = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
                when(feedRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.unlike(UUID.randomUUID(), userDetails))
                        .isInstanceOf(FeedNotExistException.class);
            }

            @Test
            @DisplayName("존재하지 않는 유저는 좋아요를 취소할 수 없다.")
            void userNotFound() {
                // given
                Feed feed = Instancio.create(Feed.class);
                User otherUser = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(otherUser);
                when(feedRepository.findById(feed.getId()))
                        .thenReturn(Optional.of(feed));
                when(userRepository.findById(otherUser.getId()))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.unlike(feed.getId(), userDetails))
                        .isInstanceOf(WeatherFitException.class);
            }

            @Test
            @DisplayName("좋아요하지 않은 피드의 좋아요는 취소할 수 없다.")
            void notLiked() {
                // given
                Feed feed = Instancio.create(Feed.class);
                User author = feed.getAuthor();
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(author);

                when(feedRepository.findById(feed.getId()))
                        .thenReturn(Optional.of(feed));
                when(userRepository.findById(author.getId()))
                        .thenReturn(Optional.of(author));
                when(feedLikeRepository.existsByFeedAndLikedUser(feed, author))
                        .thenReturn(false);

                // when & then
                assertThatThrownBy(() -> feedService.unlike(feed.getId(), userDetails))
                        .isInstanceOf(FeedLikeNotExistException.class);
            }
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class deleteComment {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Feed feed = Instancio.create(Feed.class);
            User commenter = Instancio.create(User.class);
            WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(commenter);
            Comment comment = Instancio.of(Comment.class)
                    .set(all(Feed.class), feed)
                    .set(all(User.class), commenter)
                    .create();

            when(commentRepository.findById(comment.getId()))
                    .thenReturn(Optional.of(comment));

            // when
            feedService.deleteComment(feed.getId(), comment.getId(), userDetails);

            // then
            verify(commentRepository).deleteById(comment.getId());
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("존재하지 않는 댓글은 삭제할 수 없다.")
            void commentNotFound() {
                // given
                User user = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);
                when(commentRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.deleteComment(UUID.randomUUID(), UUID.randomUUID(), userDetails))
                        .isInstanceOf(CommentNotFoundException.class);
            }

            @Test
            @DisplayName("해당 피드의 댓글이 아니면 삭제할 수 없다.")
            void feedIdMismatch() {
                // given
                Feed feed = Instancio.create(Feed.class);
                User commenter = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(commenter);
                Comment comment = Instancio.of(Comment.class)
                        .set(all(Feed.class), feed)
                        .set(all(User.class), commenter)
                        .create();
                when(commentRepository.findById(comment.getId()))
                        .thenReturn(Optional.of(comment));

                UUID differentFeedId = UUID.randomUUID();

                // when & then
                assertThatThrownBy(() -> feedService.deleteComment(differentFeedId, comment.getId(), userDetails))
                        .isInstanceOf(FeedBadRequestException.class);
            }

            @Test
            @DisplayName("댓글 작성자가 아니면 삭제할 수 없다.")
            void notCommentAuthor() {
                // given
                Feed feed = Instancio.create(Feed.class);
                Comment comment = Instancio.of(Comment.class)
                        .set(all(Feed.class), feed)
                        .create();
                User otherUser = Instancio.create(User.class);
                WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(otherUser);
                when(commentRepository.findById(comment.getId()))
                        .thenReturn(Optional.of(comment));

                // when & then
                assertThatThrownBy(() -> feedService.deleteComment(feed.getId(), comment.getId(), userDetails))
                        .isInstanceOf(FeedForbiddenException.class);
            }
        }
    }

    private void stubToFeedDtos() {
        when(feedClothesRepository.findAllFeedClothesByFeeds(anyList()))
                .thenReturn(List.of());
        when(feedLikeRepository.countByFeedIn(anyList()))
                .thenReturn(List.of());
        when(commentRepository.countByFeedIn(anyList()))
                .thenReturn(List.of());
        when(feedLikeRepository.findLikedFeedIds(anyList(), any(User.class)))
                .thenReturn(new HashSet<>());
    }

}