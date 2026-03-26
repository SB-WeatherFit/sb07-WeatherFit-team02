package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.dto.CommentDto;
import com.codeit.weatherfit.domain.feed.dto.FeedClothesDto;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;
import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import com.codeit.weatherfit.domain.feed.entity.FeedLike;
import com.codeit.weatherfit.domain.feed.exception.FeedLikeAlreadyExistException;
import com.codeit.weatherfit.domain.feed.exception.FeedLikeNotExistException;
import com.codeit.weatherfit.domain.feed.exception.FeedNotExistException;
import com.codeit.weatherfit.domain.feed.repository.CommentRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.service.UserService;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import com.codeit.weatherfit.global.s3.S3Service;
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
    private final S3Service s3Service;
    private final UserService userService;

    @Override
    @Transactional
    public FeedDto create(FeedCreateRequest request) {
        User author = getUserOrThrow(request.userId());
        Weather weather = getWeatherOrThrow(request.weatherId());
        List<Clothes> clothes = getClothesOrThrow(request.clothesIds());

        Feed feed = Feed.create(author, weather, request.content());
        Feed saved = feedRepository.save(feed);
        List<FeedClothes> coords = clothes.stream()
                .map(c -> {
                    String url = c.getImageKey() == null? null : s3Service.getUrl(c.getImageKey());
                    return FeedClothes.create(saved, c.getName(), url);
                })
                .toList();
        feedClothesRepository.saveAll(coords);
        return toFeedDto(feed);
    }

    @Override
    public FeedGetResponse getFeedsByCursor(FeedGetRequest request) {
        List<Feed> feeds = feedRepository.findWithCursor(request);
        Feed lastFeed = null;
        if (feeds.size() == request.limit() + 1) {
            feeds = feeds.subList(0, request.limit());
            lastFeed = feeds.getLast();
        }
        boolean hasNext = lastFeed != null;
        return new FeedGetResponse(
                feeds.stream()
                        .map(this::toFeedDto)
                        .toList(),
                hasNext ? lastFeed.getCreatedAt() : null,
                hasNext ? lastFeed.getId() : null,
                hasNext,
                feeds.size(),
                request.sortBy(),
                request.sortDirection()
        );
    }

    @Override
    @Transactional
    public FeedDto update(UUID id, FeedUpdateRequest request) {
        Feed feed = getFeedOrThrow(id);
        feed.update(request.content());
        return toFeedDto(feed);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentCreateRequest request) {
        Comment comment = Comment.create(
                getUserOrThrow(request.authorId()),
                getFeedOrThrow(request.feedId()),
                request.content()
        );
        Comment saved = commentRepository.save(comment);
        return CommentDto.from(saved, userService.getUserSummary(saved.getAuthor()));
    }

    @Override
    public CommentGetResponse getCommentsByCursor(CommentGetRequest request) {
        List<Comment> comments = commentRepository.getCommentsByCursor(request);

        Comment last = null;
        if (comments.size() == request.limit() + 1) {
            comments = comments.subList(0, request.limit());
            last = comments.getLast();
        }
        boolean hasNext = last != null;
        return new CommentGetResponse(
                comments.stream()
                        .map(c -> CommentDto.from(c, userService.getUserSummary(c.getAuthor())))
                        .toList(),
                hasNext? last.getCreatedAt() : null,
                hasNext? last.getId() : null,
                hasNext,
                comments.size()
        );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Feed feed = getFeedOrThrow(id);
        feedRepository.delete(feed);
    }

    @Override
    public void like(UUID id, WeatherFitUserDetails userDetails) {
        Feed feed = getFeedOrThrow(id);
        if (!feed.getAuthor().getId().equals(userDetails.getUserId()))
            throw new RuntimeException("올바르지 않은 접근입니다."); // TODO: 나중에 커스텀 에러
        User likeUser = getUserOrThrow(userDetails.getUserId());
        if (feedLikeRepository.existsByFeedAndUser(feed, likeUser))
            throw new FeedLikeAlreadyExistException(feed, likeUser);
        feedLikeRepository.save(FeedLike.create(feed, likeUser));
    }

    @Override
    public void unlike(UUID id, WeatherFitUserDetails userDetails) {
        Feed feed = getFeedOrThrow(id);
        if (!feed.getAuthor().getId().equals(userDetails.getUserId()))
            throw new RuntimeException("올바르지 않은 접근입니다."); // TODO: 나중에 커스텀 에러
        User likeUser = getUserOrThrow(userDetails.getUserId());
        if (!feedLikeRepository.existsByFeedAndUser(feed, likeUser))
            throw new FeedLikeNotExistException(feed, likeUser);
        feedLikeRepository.deleteByFeedAndUser(feed, likeUser);
    }

    private FeedDto toFeedDto(Feed feed) {
        feedClothesRepository.findAllByFeed(feed);
        return FeedDto.from(
                feed,
                feedClothesRepository.findAllByFeed(feed).stream()
                        .map(fc -> {
                            String url = fc.getImageKey() == null? null : s3Service.getUrl(fc.getImageKey());
                            return FeedClothesDto.from(fc, url);
                        }).toList(),
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
                .orElseThrow(() -> new WeatherNotFoundException(weatherId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다.")); // TODO 커스텀 에러로 수정
    }

}
