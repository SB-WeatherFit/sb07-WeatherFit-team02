package com.codeit.weatherfit.domain.feed.service.unit;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import com.codeit.weatherfit.domain.feed.repository.CommentRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.service.FeedServiceImpl;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.mockito.ArgumentMatchers.*;
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

    @InjectMocks
    private FeedServiceImpl feedService;

    @Nested
    @DisplayName("생성")
    class create {
        @Test
        @DisplayName("성공")
        void success() {
            // TODO 지금은 ClothesRepository 없이 작성된 테스트. 나중에 수정 필요.
            // given
            FeedCreateRequest request = Instancio.create(FeedCreateRequest.class);
            when(userRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(Instancio.create(User.class)));
            when(weatherRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(Instancio.create(Weather.class)));
            when(feedRepository.save(any(Feed.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            when(clothesRepository.findById(any(UUID.class)))
                    .thenAnswer(invocation -> Optional.of(Instancio.create(Clothes.class)));
            stubToFeedDto();

            // when
            FeedDto feedDto = feedService.create(request);

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
                        .isInstanceOf(IllegalArgumentException.class); // 추후 커스텀 에러로 수정
            }

            @Test
            @DisplayName("옷이 존재해야한다")
            void clothes() {
                // given
                when(userRepository.findById(any()))
                        .thenReturn(Optional.of(Instancio.create(User.class)));
                when(weatherRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(Instancio.create(Weather.class)));
                when(clothesRepository.findById(any()))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> feedService.create(Instancio.create(FeedCreateRequest.class)))
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

    private void stubToFeedDto() {
        when(feedClothesRepository.findAllByFeed(any(Feed.class)))
                .thenReturn(Instancio.createList(FeedClothes.class));
        when(feedLikeRepository.countByFeed(any(Feed.class)))
                .thenReturn(0L);
        when(commentRepository.countByFeed(any(Feed.class)))
                .thenReturn(0L);
        when(feedLikeRepository.existsByFeedAndUser(any(Feed.class), any(User.class)))
                .thenReturn(false);
    }

}