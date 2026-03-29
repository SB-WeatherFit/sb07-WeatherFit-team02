package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttribute;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.clothes.repository.SelectableValueRepository;
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
import com.codeit.weatherfit.domain.feed.exception.FeedBadRequestException;
import com.codeit.weatherfit.domain.feed.exception.FeedLikeAlreadyExistException;
import com.codeit.weatherfit.domain.feed.exception.FeedLikeNotExistException;
import com.codeit.weatherfit.domain.feed.exception.FeedNotExistException;
import com.codeit.weatherfit.domain.feed.repository.CommentRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.codeit.weatherfit.domain.follow.repository.FollowRepository;
import com.codeit.weatherfit.domain.notification.event.feed.FeedCommentedEvent;
import com.codeit.weatherfit.domain.notification.event.feed.FeedLikedEvent;
import com.codeit.weatherfit.domain.notification.event.feed.NewFeedFromFollowingsEvent;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.service.UserService;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import com.codeit.weatherfit.global.s3.S3Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
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
    private final ApplicationEventPublisher eventPublisher;
    private final FollowRepository followRepository;
    private final ClothesAttributeRepository clothesAttributeRepository;
    private final SelectableValueRepository selectableValueRepository;

    @Override
    @Transactional
    public FeedDto create(FeedCreateRequest request, WeatherFitUserDetails userDetails) {
        log.info("피드 생성 요청: userId={}, weatherId={}, clothesCount={}", request.userId(), request.weatherId(), request.clothesIds().size());
        if (!request.userId().equals(userDetails.getUserId())) {
            log.warn("피드 생성 권한 불일치: requestUserId={}, loginUserId={}", request.userId(), userDetails.getUserId());
            throw new RuntimeException("Bad request"); // 추후 인증 에러로 수정
        }
        User author = getUserOrThrow(request.userId());
        Weather weather = getWeatherOrThrow(request.weatherId());
        List<Clothes> clothes = getClothesOrThrow(request.clothesIds());

        Feed feed = Feed.create(author, weather, request.content());
        Feed saved = feedRepository.save(feed);
        List<FeedClothes> coords = clothes.stream()
                .map(c -> FeedClothes.create(saved, c))
                .toList();
        feedClothesRepository.saveAll(coords);

        publishNotiToFollowers(feed);

        log.info("피드 생성 완료: feedId={}", saved.getId());
        return toFeedDto(feed, author);
    }

    private void publishNotiToFollowers(Feed feed) {
        List<Follow> follows = followRepository.findAllByFollowee(feed.getAuthor());
        log.info("팔로워 알림 발행: feedId={}, followerCount={}", feed.getId(), follows.size());
        String followeeName = feed.getAuthor().getName();
        String feedContent = feed.getContent();
        follows.stream().map(Follow::getFollower).forEach(follower -> {
            log.debug("팔로워 알림 이벤트 발행: followerId={}, feedId={}", follower.getId(), feed.getId());
            eventPublisher.publishEvent(new NewFeedFromFollowingsEvent(
                    follower.getId(),
                    followeeName,
                    feedContent
            ));
        });
    }

    @Override
    public FeedGetResponse getFeedsByCursor(FeedGetRequest request, WeatherFitUserDetails userDetails) {
        log.info("피드 커서 조회: sortBy={}, sortDirection={}, limit={}", request.sortBy(), request.sortDirection(), request.limit());
        List<Feed> feeds = feedRepository.findWithCursor(request);
        User loginUser = getUserOrThrow(userDetails.getUserId());
        Feed lastFeed = null;
        if (feeds.size() == request.limit() + 1) {
            feeds = feeds.subList(0, request.limit());
            lastFeed = feeds.getLast();
        }
        boolean hasNext = lastFeed != null;
        log.info("피드 커서 조회 완료: count={}, hasNext={}", feeds.size(), hasNext);
        return new FeedGetResponse(
                feeds.stream()
                        .map(f -> this.toFeedDto(f, loginUser))
                        .toList(),
                hasNext ? lastFeed.getCreatedAt() : null,
                hasNext ? lastFeed.getId() : null,
                hasNext,
                feedRepository.count(),
                request.sortBy(),
                request.sortDirection()
        );
    }

    @Override
    @Transactional
    public FeedDto update(UUID id, FeedUpdateRequest request, WeatherFitUserDetails userDetails) {
        log.info("피드 수정 요청: feedId={}", id);
        Feed feed = getFeedOrThrow(id);
        if (!userDetails.getUserId().equals(feed.getAuthor().getId())) {
            log.warn("피드 수정 권한 불일치: feedId={}, authorId={}, loginUserId={}", id, feed.getAuthor().getId(), userDetails.getUserId());
            throw new RuntimeException("Bad Request"); // 추후 인증 오류로 변경
        }
        feed.update(request.content());
        log.info("피드 수정 완료: feedId={}", id);
        return toFeedDto(feed, feed.getAuthor());
    }

    @Override
    @Transactional
    public CommentDto createComment(UUID id, CommentCreateRequest request, WeatherFitUserDetails userDetails) {
        log.info("댓글 생성 요청: authorId={}, feedId={}", request.authorId(), request.feedId());
        if (!id.equals(request.feedId())) {
            log.warn("댓글 생성 feedId 불일치: pathId={}, requestFeedId={}", id, request.feedId());
            throw new FeedBadRequestException();
        }
        if (!userDetails.getUserId().equals(request.authorId())) {
            log.warn("댓글 생성 권한 불일치: requestAuthorId={}, loginUserId={}", request.authorId(), userDetails.getUserId());
            throw new RuntimeException("Bad Request"); // 추후 인증 오류로 변경
        }
        User commenter = getUserOrThrow(request.authorId());
        Feed feed = getFeedOrThrow(request.feedId());
        Comment comment = Comment.create(
                commenter,
                feed,
                request.content()
        );
        Comment saved = commentRepository.save(comment);
        log.info("댓글 생성 완료: commentId={}", saved.getId());

        log.info("댓글 알림 이벤트 발행: feedAuthorId={}, commenterId={}", feed.getAuthor().getId(), commenter.getId());
        eventPublisher.publishEvent(new FeedCommentedEvent(
                feed.getAuthor().getId(),
                commenter.getName(),
                comment.getContent()
        ));

        return CommentDto.from(saved, userService.getUserSummary(saved.getAuthor()));
    }

    @Override
    public CommentGetResponse getCommentsByCursor(CommentGetRequest request, WeatherFitUserDetails userDetails) {
        log.info("댓글 커서 조회: feedId={}, limit={}", request.feedId(), request.limit());
        List<Comment> comments = commentRepository.getCommentsByCursor(request);

        Comment last = null;
        if (comments.size() == request.limit() + 1) {
            comments = comments.subList(0, request.limit());
            last = comments.getLast();
        }
        boolean hasNext = last != null;
        log.info("댓글 커서 조회 완료: count={}, hasNext={}", comments.size(), hasNext);
        return new CommentGetResponse(
                comments.stream()
                        .map(c -> CommentDto.from(c, userService.getUserSummary(c.getAuthor())))
                        .toList(),
                hasNext ? last.getCreatedAt() : null,
                hasNext ? last.getId() : null,
                hasNext,
                commentRepository.count()
        );
    }

    @Override
    @Transactional
    public void delete(UUID id, WeatherFitUserDetails userDetails) {
        log.info("피드 삭제 요청: feedId={}", id);
        Feed feed = getFeedOrThrow(id);
        if (!userDetails.getUserId().equals(feed.getAuthor().getId())) {
            log.warn("피드 삭제 권한 불일치: feedId={}, authorId={}, loginUserId={}", id, feed.getAuthor().getId(), userDetails.getUserId());
            throw new RuntimeException("Bad Request"); // 추후 인증 오류로 수정
        }
        feedRepository.delete(feed);
        log.info("피드 삭제 완료: feedId={}", id);
    }

    @Override
    @Transactional
    public void like(UUID id, WeatherFitUserDetails userDetails) {
        log.info("피드 좋아요 요청: feedId={}, userId={}", id, userDetails.getUserId());
        Feed feed = getFeedOrThrow(id);
        User likeUser = getUserOrThrow(userDetails.getUserId());
        if (feedLikeRepository.existsByFeedAndLikedUser(feed, likeUser))
            throw new FeedLikeAlreadyExistException(feed, likeUser);
        feedLikeRepository.save(FeedLike.create(feed, likeUser));

        log.info("좋아요 알림 이벤트 발행: feedAuthorId={}, likeUserId={}", feed.getAuthor().getId(), likeUser.getId());
        eventPublisher.publishEvent(new FeedLikedEvent(
                feed.getAuthor().getId(),
                likeUser.getName(),
                feed.getContent()
        ));

        log.info("피드 좋아요 완료: feedId={}, userId={}", id, userDetails.getUserId());
    }

    @Override
    @Transactional
    public void unlike(UUID id, WeatherFitUserDetails userDetails) {
        log.info("피드 좋아요 취소 요청: feedId={}, userId={}", id, userDetails.getUserId());
        Feed feed = getFeedOrThrow(id);
        User likeUser = getUserOrThrow(userDetails.getUserId());
        if (!feedLikeRepository.existsByFeedAndLikedUser(feed, likeUser))
            throw new FeedLikeNotExistException(feed, likeUser);
        feedLikeRepository.deleteByFeedAndLikedUser(feed, likeUser);
        log.info("피드 좋아요 취소 완료: feedId={}, userId={}", id, userDetails.getUserId());
    }

    private FeedDto toFeedDto(Feed feed, User loginUser) {
        return FeedDto.from(
                feed,
                feedClothesRepository.findAllByFeed(feed).stream()
                        .map(this::getFeedClothesDto).toList(),
                feedLikeRepository.countByFeed(feed),
                commentRepository.countByFeed(feed),
                feedLikeRepository.existsByFeedAndLikedUser(feed, loginUser)
        );
    }

    private @NonNull FeedClothesDto getFeedClothesDto(FeedClothes fc) {
        Clothes c = clothesRepository.findById(fc.getClothes().getId())
                .orElseThrow();
        List<ClothesAttribute> byClothes = clothesAttributeRepository.findByClothes(c);
        List<ClothesAttributeWithDefDto> defDtoList = byClothes.stream()
                .map(this::getClothesAttributeWithDefDto)
                .toList();
        String imageKey = fc.getClothes().getImageKey();
        String url = imageKey == null ? null : s3Service.getUrl(imageKey);
        return FeedClothesDto.from(fc, url, defDtoList);
    }

    private ClothesAttributeWithDefDto getClothesAttributeWithDefDto(ClothesAttribute c) {
        ClothesAttributeType clothesAttributeType = c.getOption().getClothesAttributeType();
        UUID defId = clothesAttributeType.getId();
        String defName = clothesAttributeType.getName();
        List<String> selectableValues = selectableValueRepository.findByClothesAttributeType(clothesAttributeType).stream()
                .map(SelectableValue::getOption) // 순서?
                .toList();
        String option = c.getOption().getOption();
        return new ClothesAttributeWithDefDto(
                defId,
                defName,
                selectableValues,
                option
        );
    }

    private Feed getFeedOrThrow(UUID id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("피드 조회 실패: feedId={}", id);
                    return new FeedNotExistException(id);
                });
    }

    private List<Clothes> getClothesOrThrow(List<UUID> clothesIds) {
        List<Clothes> clothes = clothesRepository.findAllById(clothesIds);
        if (clothes.size() != clothesIds.size()) {
            log.warn("존재하지 않는 옷 포함: 요청={}, 조회={}", clothesIds.size(), clothes.size());
            throw new IllegalArgumentException("존재하지 않는 옷이 포함되어 있습니다.");
        }
        return clothes;
    }

    private Weather getWeatherOrThrow(UUID weatherId) {
        return weatherRepository.findById(weatherId)
                .orElseThrow(() -> {
                    log.warn("날씨 조회 실패: weatherId={}", weatherId);
                    return new WeatherNotFoundException(weatherId);
                });
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("유저 조회 실패: userId={}", userId);
                    return new IllegalArgumentException("존재하지 않는 id입니다.");
                }); // TODO 커스텀 에러로 수정
    }

}
