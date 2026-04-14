package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.exception.ClothesNotFoundException;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.dto.CommentDto;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.Ootd;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;
import com.codeit.weatherfit.domain.feed.entity.*;
import com.codeit.weatherfit.domain.feed.event.FeedCreatedEvent;
import com.codeit.weatherfit.domain.feed.event.FeedDeletedEvent;
import com.codeit.weatherfit.domain.feed.event.FeedUpdatedEvent;
import com.codeit.weatherfit.domain.feed.exception.*;
import com.codeit.weatherfit.domain.feed.repository.CommentRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.service.search.FeedSearchService;
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
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import com.codeit.weatherfit.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private final FeedSearchService feedSearchService;

    @Override
    @Transactional
    public FeedDto create(FeedCreateRequest request, WeatherFitUserDetails userDetails) {
        UUID requestedId = request.authorId();
        UUID loginUserId = userDetails.getUserId();
        log.info("피드 생성 요청: authorId={}, weatherId={}, clothesCount={}", requestedId, request.weatherId(), request.clothesIds().size());
        if (!requestedId.equals(loginUserId)) {
            log.warn("피드 생성 권한 불일치: requestUserId={}, loginUserId={}", requestedId, loginUserId);
            throw new FeedForbiddenException(requestedId, loginUserId);
        }
        User author = getUserOrThrow(requestedId);
        Weather weather = getWeatherOrThrow(request.weatherId());
        List<Clothes> clothes = getClothesOrThrow(request.clothesIds());

        Feed feed = Feed.create(author, weather, request.content());
        Feed saved = feedRepository.save(feed);
        List<FeedClothes> coords = clothes.stream()
                .map(c -> FeedClothes.create(
                        saved,
                        c,
                        clothesAttributeRepository.getClothesOptions((c))))
                .toList();
        feedClothesRepository.saveAll(coords);

        publishNotiToFollowers(feed);
        eventPublisher.publishEvent(new FeedCreatedEvent(saved.getId()));

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
        List<Feed> feeds;

        if (request.keywordLike() != null && !request.keywordLike().isBlank()) {
            List<UUID> feedIds = feedSearchService.searchFeeds(request);
            feeds = feedIds.isEmpty()
                    ? List.of()
                    : feedRepository.findAllById(feedIds);
            // ES 순서대로 재정렬
            Map<UUID, Feed> feedMap = new HashMap<>();
            feeds.forEach(f -> feedMap.put(f.getId(), f));
            feeds = feedIds.stream()
                    .map(feedMap::get)
                    .toList();
        } else {
            feeds = feedRepository.findWithCursor(request);
        }

        User loginUser = getUserOrThrow(userDetails.getUserId());
        Feed lastFeed = null;
        if (feeds.size() == request.limit() + 1) {
            feeds = feeds.subList(0, request.limit());
            lastFeed = feeds.getLast();
        }
        boolean hasNext = lastFeed != null;
        log.info("피드 커서 조회 완료: count={}, hasNext={}", feeds.size(), hasNext);
        return new FeedGetResponse(
                toFeedDtos(feeds, loginUser),
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
        UUID authorId = feed.getAuthor().getId();
        UUID loginUserId = userDetails.getUserId();
        if (!loginUserId.equals(authorId)) {
            log.warn("피드 수정 권한 불일치: feedId={}, authorId={}, loginUserId={}", id, authorId, loginUserId);
            throw new FeedForbiddenException(authorId, loginUserId); // 추후 인증 오류로 변경
        }
        feed.update(request.content());
        eventPublisher.publishEvent(FeedUpdatedEvent.contentUpdated(feed.getId(), request.content()));
        log.info("피드 수정 완료: feedId={}", id);
        return toFeedDto(feed, feed.getAuthor());
    }

    @Override
    @Transactional
    @CacheEvict(value = "feedCommentCount", key = "#id.toString()")
    public CommentDto createComment(UUID id, CommentCreateRequest request, WeatherFitUserDetails userDetails) {
        UUID authorId = request.authorId();
        UUID feedId = request.feedId();
        UUID loginUserId = userDetails.getUserId();
        log.info("댓글 생성 요청: authorId={}, feedId={}", authorId, feedId);
        if (!id.equals(feedId)) {
            log.warn("댓글 생성 feedId 불일치: pathId={}, requestFeedId={}", id, feedId);
            throw new FeedBadRequestException();
        }
        if (!loginUserId.equals(authorId)) {
            log.warn("댓글 생성 권한 불일치: requestAuthorId={}, loginUserId={}", authorId, loginUserId);
            throw new FeedForbiddenException(authorId, loginUserId);
        }
        User commenter = getUserOrThrow(authorId);
        Feed feed = getFeedOrThrow(feedId);
        Comment comment = Comment.create(
                commenter,
                feed,
                request.content()
        );
        Comment saved = commentRepository.save(comment);
        log.info("댓글 생성 완료: commentId={}", saved.getId());

        // 자기 자신이 덧글을 단 경우엔 알람 발생 X
        if (!commenter.getId().equals(feed.getAuthor().getId())) {
            log.info("댓글 알림 이벤트 발행: feedAuthorId={}, commenterId={}", feed.getAuthor().getId(), commenter.getId());
            eventPublisher.publishEvent(new FeedCommentedEvent(
                    feed.getAuthor().getId(),
                    commenter.getName(),
                    comment.getContent()
            ));
        }

        return CommentDto.from(saved, userService.getUserSummary(saved.getAuthor()));
    }

    @Override
    @Transactional
    @CacheEvict(value = "feedCommentCount", key = "#id.toString()")
    public void deleteComment(UUID id, UUID commentId, WeatherFitUserDetails userDetails) {
        log.info("댓글 삭제 요청: feedId={}, commentId={}", id, commentId);
        Comment comment = getCommentOrThrow(commentId);
        UUID feedId = comment.getFeed().getId();
        UUID commentAuthorId = comment.getAuthor().getId();
        UUID loginUserId = userDetails.getUserId();
        if (!id.equals(feedId)) {
            log.warn("댓글 삭제 feedId 불일치: pathFeedId={}, commentFeedId={}", id, feedId);
            throw new FeedBadRequestException("해당 feed의 comment가 아닙니다.");
        }
        if (!commentAuthorId.equals(loginUserId)) {
            log.warn("댓글 삭제 권한 불일치: commentAuthorId={}, loginUserId={}", commentAuthorId, loginUserId);
            throw new FeedForbiddenException(commentAuthorId, loginUserId);
        }
        commentRepository.deleteById(commentId);
        log.info("댓글 삭제 완료: commentId={}", commentId);
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
        UUID feedAuthorId = feed.getAuthor().getId();
        UUID loginUserId = userDetails.getUserId();
        if (!loginUserId.equals(feedAuthorId)) {
            log.warn("피드 삭제 권한 불일치: feedId={}, authorId={}, loginUserId={}", id, feedAuthorId, loginUserId);
            throw new FeedForbiddenException(feedAuthorId, loginUserId);
        }
        feedClothesRepository.deleteByFeed(feed);
        commentRepository.deleteByFeed(feed);
        eventPublisher.publishEvent(new FeedDeletedEvent(feed.getId()));
        feedRepository.delete(feed);
        log.info("피드 삭제 완료: feedId={}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "feedLikeCount", key = "#id.toString()")
    public void like(UUID id, WeatherFitUserDetails userDetails) {
        log.info("피드 좋아요 요청: feedId={}, authorId={}", id, userDetails.getUserId());
        Feed feed = getFeedOrThrow(id);
        User likeUser = getUserOrThrow(userDetails.getUserId());
        if (feedLikeRepository.existsByFeedAndLikedUser(feed, likeUser))
            throw new FeedLikeAlreadyExistException(feed, likeUser);
        feedLikeRepository.save(FeedLike.create(feed, likeUser));

        // 자기 자신이 좋아요를 한 경우엔 알람 발생 X
        if (!likeUser.getId().equals(feed.getAuthor().getId())) {
            log.info("좋아요 알림 이벤트 발행: feedAuthorId={}, likeUserId={}", feed.getAuthor().getId(), likeUser.getId());
            eventPublisher.publishEvent(new FeedLikedEvent(
                    feed.getAuthor().getId(),
                    likeUser.getName(),
                    feed.getContent()
            ));
        }

        eventPublisher.publishEvent(FeedUpdatedEvent.liked(feed.getId()));

        log.info("피드 좋아요 완료: feedId={}, authorId={}", id, userDetails.getUserId());
    }

    @Override
    @Transactional
    @CacheEvict(value = "feedLikeCount", key = "#id.toString()")
    public void unlike(UUID id, WeatherFitUserDetails userDetails) {
        log.info("피드 좋아요 취소 요청: feedId={}, authorId={}", id, userDetails.getUserId());
        Feed feed = getFeedOrThrow(id);
        User likeUser = getUserOrThrow(userDetails.getUserId());
        if (!feedLikeRepository.existsByFeedAndLikedUser(feed, likeUser))
            throw new FeedLikeNotExistException(feed, likeUser);
        feedLikeRepository.deleteByFeedAndLikedUser(feed, likeUser);

        eventPublisher.publishEvent(FeedUpdatedEvent.unliked(feed.getId()));

        log.info("피드 좋아요 취소 완료: feedId={}, authorId={}", id, userDetails.getUserId());
    }

    private FeedDto toFeedDto(Feed feed, User loginUser) {
        return toFeedDtos(List.of(feed), loginUser).getFirst();
    }

    private List<FeedDto> toFeedDtos(List<Feed> feeds, User loginUser) {
        if(feeds.isEmpty()) return List.of();

        List<UUID> feedIds = feeds.stream().map(Feed::getId).toList();
        Map<UUID, List<ClothesSnapshot>> feedMap = loadSnapshotMap(feedIds);
        Map<UUID, Long> likeMap = loadLikeCountMap(feedIds);
        Map<UUID, Long> commentMap = loadCommentCountMap(feedIds);
        Set<UUID> likedFeedSet = feedLikeRepository.findLikedFeedIds(feedIds, loginUser);

        return feeds.stream()
                .map(feed -> FeedDto.from(
                        feed,
                        toOotds(feedMap.getOrDefault(feed.getId(), List.of())),
                        likeMap.getOrDefault(feed.getId(), 0L),
                        commentMap.getOrDefault(feed.getId(), 0L),
                        likedFeedSet.contains(feed.getId())
                )).toList();
    }

    private List<Ootd> toOotds(List<ClothesSnapshot> clothesSnapshots) {
        return clothesSnapshots.stream()
                .map(cs -> cs.imageKey() == null? null : Ootd.from(cs, s3Service.getUrl(cs.imageKey())))
                .toList();
    }

    private Map<UUID, Long> loadCommentCountMap(List<UUID> feedIds) {
        return commentRepository.countByFeedIn(feedIds).stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (Long) row[1]
                ));
    }

    private Map<UUID, Long> loadLikeCountMap(List<UUID> feedIds) {
        return feedLikeRepository.countByFeedIn(feedIds).stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (Long) row[1]
                ));
    }

    private Map<UUID, List<ClothesSnapshot>> loadSnapshotMap(List<UUID> feedIds) {
        return feedClothesRepository.findAllFeedClothesByFeeds(feedIds).stream()
                .collect(Collectors.groupingBy(
                        fc -> fc.getFeed().getId(),
                        Collectors.mapping(FeedClothes::getClothesSnapshot, Collectors.toList())
                ));
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
            throw new ClothesNotFoundException(ErrorCode.CLOTHES_NOT_FOUND);
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
                    log.warn("유저 조회 실패: authorId={}", userId);
                    return new WeatherFitException(ErrorCode.USER_NOT_FOUND);
                });
    }

    private Comment getCommentOrThrow(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}
