package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequestDto;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedServiceImpl implements FeedService {

    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final FeedRepository feedRepository;
    private final FeedClothesRepository feedClothesRepository;

    @Override
    public FeedDto create(FeedCreateRequestDto requestDto) {
        User author = getUserOrThrow(requestDto.userId());
        Weather weather = getWeatherOrThrow(requestDto.weatherId());
        List<Clothes> clothes = getClothesOrThrow(requestDto.clothesIds());

        Feed feed = Feed.create(author, weather, requestDto.content());
        Feed saved = feedRepository.save(feed);
        List<FeedClothes> coords = clothes.stream()
                .map(c -> FeedClothes.create(saved, c.getName(), c.getImageUrl()))
                .toList();
        feedClothesRepository.saveAll(coords);
        return FeedDto.from(saved, coords, 0L, 0L, false);
    }

    @Override
    public FeedDto findById(UUID id) {
        return null;
    }

    @Override
    public List<FeedDto> findAllByUserId(UUID userId) {
        return List.of();
    }

    private List<Clothes> getClothesOrThrow(List<UUID> clothesIds) {
        return List.of();
//        return clothesIds.stream()
//                .map(clothesRepository.findById(clothesIds)
//                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다.")))
//                .toList();// TODO 커스텀 에러로 수정
    }

    private Weather getWeatherOrThrow(UUID weatherId) {
        return weatherRepository.findById(weatherId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다.")); // TODO 커스텀 에러로 수정
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다.")); // TODO 커스텀 에러로 수정
    }
}
