package com.codeit.weatherfit.domain.follow.service;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.codeit.weatherfit.domain.follow.entity.FollowCreateParam;
import com.codeit.weatherfit.domain.follow.exception.AlreadyFollowException;
import com.codeit.weatherfit.domain.follow.exception.FollowUserNotExistException;
import com.codeit.weatherfit.domain.follow.exception.NotExistFollowException;
import com.codeit.weatherfit.domain.follow.repository.FollowRepository;
import com.codeit.weatherfit.domain.notification.event.follow.FollowerCreatedEvent;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.service.UserService;
import com.codeit.weatherfit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Override
    @Transactional
    public FollowDto follow(FollowCreateRequest createRequest) {
        if (checkFollowExist(createRequest.followeeId(), createRequest.followerId())) {
            throw new AlreadyFollowException();
        }

        User followee = userRepository.findById(createRequest.followeeId()).orElseThrow();
        User follower = userRepository.findById(createRequest.followerId()).orElseThrow();


        Follow follow = Follow.create(new FollowCreateParam(followee, follower));
        Follow save = followRepository.save(follow);

        eventPublisher.publishEvent(new FollowerCreatedEvent(followee.getId(), follower.getName()));

        return FollowDto.create(save.getId(), userService.getUserSummary(followee), userService.getUserSummary(follower));
    }

    @Override
    public FollowSummaryDto getFollowSummary(UUID userId, UUID myId) {
        checkExistUser(userId);

        long followerCount = followRepository.getFollowerCount(userId);
        long followingCount = followRepository.getFollowingCount(userId);
        boolean followedByMe = false;
        UUID followId = null;

        Optional<Follow> optionalFollow = followRepository.findByFolloweeIdAndFollowerId(userId, myId);
        if (optionalFollow.isPresent()) {
            followedByMe = true;
            followId = optionalFollow.get().getId();
        }

        boolean followingMe = checkFollowExist(myId, userId);
        return FollowSummaryDto.create(userId, followerCount, followingCount, followedByMe, followId, followingMe);
    }

    private void checkExistUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new FollowUserNotExistException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private boolean checkFollowExist(UUID followeeId, UUID followerId) {
        return followRepository.existsByFolloweeIdAndFollowerId(followeeId, followerId);
    }

    @Override
    public FollowListResponse getFollowees(FolloweeSearchCondition condition) {
        List<Follow> follows = followRepository.searchFollowees(condition);
        long totalCount = followRepository.countByFollowerId(condition.followerId());

        Instant nextCursor = null;
        UUID nextIdAfter;
        boolean hasNext = false;

        if (follows.size() > condition.limit()) {
            follows.removeLast();
            hasNext = true;
            nextCursor = follows.getLast().getCreatedAt();
        }

        nextIdAfter = follows.getLast().getFollowee().getId();

        List<FollowDto> data = follows.stream()
                .map(follow -> FollowDto.create(follow.getId(),
                        userService.getUserSummary(follow.getFollowee()),
                        userService.getUserSummary(follow.getFollower()))
                )
                .toList();

        return new FollowListResponse(data, nextCursor, nextIdAfter, hasNext, totalCount);
    }

    @Override
    public FollowListResponse getFollowers(FollowerSearchCondition condition) {
        List<Follow> follows = followRepository.searchFollowers(condition);
        long totalCount = followRepository.countByFolloweeId(condition.followeeId());

        Instant nextCursor = null;
        UUID nextIdAfter;
        boolean hasNext = false;

        if (follows.size() > condition.limit()) {
            follows.removeLast();
            hasNext = true;
            nextCursor = follows.getLast().getCreatedAt();
        }

        nextIdAfter = follows.getLast().getFollowee().getId();

        List<UUID> ids = follows.stream().map(follow -> follow.getFollower().getId()).toList();
//        Map<UUID, Profile> collect = profileRepository.findByUserIds(ids).stream()
//                .collect(Collectors.toMap(
//                        p -> p.getUser().getId(),
//                        p -> p
//                ));
//        Profile profile = profileRepository.findByUserId(condition.followeeId()).orElseThrow(() -> new FollowProfileNotExistException(ErrorCode.PROFILE_NOT_FOUND));

        // TODO : N + 1 문제 해결 필요
        List<FollowDto> data = follows.stream()
                .map(follow -> FollowDto.create(follow.getId(),
                        userService.getUserSummary(follow.getFollowee()),
                        userService.getUserSummary(follow.getFollower()))
                )
                .toList();

        return new FollowListResponse(data, nextCursor, nextIdAfter, hasNext, totalCount);
    }

    @Override
    @Transactional
    public void unFollow(UUID followId) {
        Follow follow = followRepository.findById(followId).orElseThrow(NotExistFollowException::new);

        followRepository.delete(follow);
    }
}
