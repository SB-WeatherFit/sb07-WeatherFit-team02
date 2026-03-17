package com.codeit.weatherfit.domain.follow.service;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.codeit.weatherfit.domain.follow.entity.FollowCreateParam;
import com.codeit.weatherfit.domain.follow.exception.AlreadyFollowException;
import com.codeit.weatherfit.domain.follow.exception.NotExistFollowException;
import com.codeit.weatherfit.domain.follow.repository.FollowRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService{

    private final FollowRepository followRepository;
//    private final ProfileRepository profileRepository; // todo: profileRepository 추가되면
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FollowDto follow(FollowCreateRequest createRequest) {
         if (checkFollowExist(createRequest.followeeId(), createRequest.followerId())){
             throw new AlreadyFollowException();
         }

        User followee = userRepository.findById(createRequest.followeeId()).orElseThrow();
        User follower = userRepository.findById(createRequest.followerId()).orElseThrow();


        Follow follow = Follow.create(new FollowCreateParam(followee, follower));
        Follow save = followRepository.save(follow);

        return FollowDto.create(save.getId(), null, null); // todo: profileRepository 추가 시 수정
    }

    @Override
    public FollowSummaryDto getFollowSummary(UUID userId, UUID myId) {
        // todo: userId를 통해 존재하는 유저인지 검증 -> existsById 추가
        long followerCount = followRepository.getFollowerCount(userId);
        long followingCount = followRepository.getFollowingCount(userId);
        boolean followedByMe = false;
        UUID followId = null;

        Optional<Follow> optionalFollow = followRepository.findByFolloweeIdAndFollowerId(userId, myId);
        if(optionalFollow.isPresent()){
            followedByMe = true;
            followId =  optionalFollow.get().getId();
        }

        boolean followingMe = checkFollowExist(myId, userId);
        return FollowSummaryDto.create(userId, followerCount, followingCount, followedByMe, followId, followingMe);
    }

    private boolean checkFollowExist(UUID followeeId, UUID followerId) {
        return followRepository.existsByFolloweeIdAndFollowerId(followeeId, followerId);
    }

    @Override
    public FollowListResponse getFollowees(FolloweeSearchCondition condition) {
        List<Follow> follows = followRepository.searchFollowees(condition);
        long totalCount = followRepository.countByFolloweeId(condition.followeeId());

        Instant nextCursor = null;
        UUID nextIdAfter ;
        boolean hasNext = false;

        if(follows.size() >condition.limit()){
            follows.removeLast();
            hasNext = true;
            nextCursor = follows.getLast().getCreatedAt();
        }

        nextIdAfter = follows.getLast().getFollowee().getId();

        List<FollowDto> data = follows.stream()
                .map(follow ->  FollowDto.create(follow.getId(), null, null))
                .toList();

        // todo: ProfileRepository 쪽에서 List<Profile> 로 데이터 조회
//        public List<Profile> findAllByUserIds(List<UUID> userIds) {
//            return queryFactory
//                    .selectFrom(profile)
//                    .where(profile.user.id.in(userIds))
//                    .fetch();
//        }
        // todo: ids 만들기
//        data.stream().map(followDto -> followDto.)
//        Map<UUID, Profile> profileMap = profileRepository.findAllByUserIdIn(userIds).stream()
//            .collect(Collectors.toMap(
//                p -> p.getUser().getId(), // Key: 유저 ID
//                p -> p                    // Value: Profile 객체 자신
//            ));


        return new FollowListResponse(data, nextCursor, nextIdAfter, hasNext, totalCount);
    }

    @Override
    public FollowListResponse getFollowers(FollowerSearchCondition condition) {
        List<Follow> follows = followRepository.searchFollowers(condition);
        long totalCount = followRepository.countByFollowerId(condition.followerId());


        return null;
    }

    @Override
    @Transactional
    public void unFollow(UUID followId) {
        Follow follow = followRepository.findById(followId).orElseThrow(NotExistFollowException::new);

        followRepository.delete(follow);
    }
}
