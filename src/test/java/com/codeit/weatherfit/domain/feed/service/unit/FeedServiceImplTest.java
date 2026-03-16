package com.codeit.weatherfit.domain.feed.service.unit;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private FeedClothesRepository feedClothesRepository;

    @InjectMocks
    private FeedServiceImpl feedService;

    @Nested
    @DisplayName("피드 생성")
    class create{
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

            // when
            FeedDto feedDto = feedService.create(request);

            // then
            assertThat(feedDto.content()).isEqualTo(request.content());
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
        /* TODO: 추후 추가
        @Test
        @DisplayName("옷이 존재해야한다")
        void clothes() {
            // given
            when(userRepository.findById(any()))
                    .thenReturn(Optional.of(Instancio.create(User.class)));
            when(weatherRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(instancio.create(Weather.class)));
            when(clothesRepository.findById(any())
                    .thenReturn(Optional.of());

            // when & then
            assertThatThrownBy(() -> feedService.create(Instancio.create(FeedCreateRequestDto.class)))
                    .isInstanceOf(IllegalArgumentException.class); // 추후 커스텀 에러로 수정
        }
        */
        }
    }




}