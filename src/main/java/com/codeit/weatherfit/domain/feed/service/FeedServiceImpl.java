package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import com.codeit.weatherfit.domain.feed.exception.FeedNotExistException;
import com.codeit.weatherfit.domain.feed.repository.CommentRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FeedLikeRepository feedLikeRepository;
    private final CommentRepository commentRepository;
    private final ClothesRepository clothesRepository;

    @Override
    @Transactional
    public FeedDto create(FeedCreateRequest requestDto) {
        User author = getUserOrThrow(requestDto.userId());
        Weather weather = getWeatherOrThrow(requestDto.weatherId());
        List<Clothes> clothes = getClothesOrThrow(requestDto.clothesIds());

        Feed feed = Feed.create(author, weather, requestDto.content());
        Feed saved = feedRepository.save(feed);
        List<FeedClothes> coords = clothes.stream()
                .map(c -> FeedClothes.create(saved, c.getName(), c.getImageUrl()))
                .toList();
        feedClothesRepository.saveAll(coords);
        return toFeedDto(feed);
    }

    @Override
    public FeedDto findById(UUID id) {
        return null; // TODO
    }

    @Override
    public List<FeedDto> findAllByUserId(UUID userId) {
        return List.of(); // TODO
    }

    @Override
    @Transactional
    public FeedDto update(UUID id, FeedUpdateRequest requestDto) {
        Feed feed = getFeedOrThrow(id);
        feed.update(requestDto.content());
        return toFeedDto(feed);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Feed feed = getFeedOrThrow(id);
        feedRepository.delete(feed);
    }

    private FeedDto toFeedDto(Feed feed) {
        return FeedDto.from(
                feed,
                feedClothesRepository.findAllByFeed(feed),
                feedLikeRepository.countByFeed(feed),
                commentRepository.countByFeed(feed),
                feedLikeRepository.existsByFeedAndUser(feed, feed.getAuthor()) // TODO 인증 구현 후 현재 로그인 유저로 변경
        );
    }

    private Feed getFeedOrThrow(UUID id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> new FeedNotExistException(id));
    }

    private List<Clothes> getClothesOrThrow(List<UUID> clothesIds) {
        List<Clothes> clothes = clothesRepository.findAllById(clothesIds);
        if (clothes.size() != clothesIds.size())
            throw new IllegalArgumentException("존재하지 않는 옷이 포함되어 있습니다.");
        return clothes;
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
