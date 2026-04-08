package com.codeit.weatherfit.global.s3;

import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.global.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Scheduler {
    private final S3Properties s3Properties;
    private final S3Client s3Client;
    private final ClothesRepository clothesRepository;
    private final ProfileRepository profileRepository;
    private final FeedRepository feedRepository;
    private final FeedClothesRepository feedClothesRepository;

    @Scheduled(cron = "0 0 0 * * mon") // 일주일에 한번 고아 이미지 파일 삭제
    public void deleteImages() {
        log.info("S3 고아 이미지 정리 시작");
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(s3Properties.bucket())
                .prefix("images/")
                .build();

        Set<String> keys = new HashSet<>();
        while (true) {
            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            keys.addAll(response.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toSet()));
            if (!response.isTruncated())
                break;
            request = request.toBuilder().continuationToken(response.nextContinuationToken()).build();
        }
        log.info("S3 전체 이미지 수: {}", keys.size());

        Set<String> imageKeys = new HashSet<>();
        imageKeys.addAll(profileRepository.findAllImageKeys());
        imageKeys.addAll(clothesRepository.findAllImageKeys());
        imageKeys.addAll(feedClothesRepository.findAllImageKeys());
        log.info("DB 참조 이미지 수: {}", imageKeys.size());

        keys.removeAll(imageKeys);
        log.info("삭제 대상 고아 이미지 수: {}", keys.size());

        if (keys.isEmpty()) {
            log.info("삭제할 고아 이미지 없음, 종료");
            return;
        }

        List<ObjectIdentifier> objectIds = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();
        for (int i = 0; i < objectIds.size(); i += 1000) {
            List<ObjectIdentifier> batch = objectIds.subList(i, Math.min(i + 1000, objectIds.size()));
            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(s3Properties.bucket())
                    .delete(Delete.builder().objects(batch).build())
                    .build());
            log.info("배치 삭제 완료: {}건", batch.size());
        }
        log.info("S3 고아 이미지 정리 완료: 총 {}건 삭제", objectIds.size());
    }
}
