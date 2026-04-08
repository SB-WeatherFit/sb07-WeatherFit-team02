package com.codeit.weatherfit.global.s3;

import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedClothesRepository;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.global.s3.properties.S3Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3SchedulerTest {

    @Mock
    private S3Properties s3Properties;

    @Mock
    private S3Client s3Client;

    @Mock
    private ClothesRepository clothesRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private FeedClothesRepository feedClothesRepository;

    @InjectMocks
    private S3Scheduler s3Scheduler;

    private void stubBucket() {
        when(s3Properties.bucket()).thenReturn("test-bucket");
    }

    private void stubS3Keys(String... keys) {
        List<S3Object> objects = new ArrayList<>();
        for (String key : keys) {
            objects.add(S3Object.builder().key(key).build());
        }
        ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(objects)
                .isTruncated(false)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);
    }

    private void stubRepositories(Set<String> profileKeys, Set<String> clothesKeys, List<String> feedClothesKeys) {
        when(profileRepository.findAllImageKeys()).thenReturn(profileKeys);
        when(clothesRepository.findAllImageKeys()).thenReturn(clothesKeys);
        when(feedClothesRepository.findAllImageKeys()).thenReturn(feedClothesKeys);
    }

    @Nested
    @DisplayName("deleteImages")
    class DeleteImages {

        @Nested
        @DisplayName("고아 이미지 존재")
        class OrphansExist {

            @Test
            @DisplayName("DB에 참조되지 않는 키만 삭제한다")
            void deletesOnlyUnreferencedKeys() {
                stubBucket();
                stubS3Keys("images/profile.jpg", "images/clothes.jpg", "images/feedClothes.jpg", "images/orphan.jpg");
                stubRepositories(
                        Set.of("images/profile.jpg"),
                        Set.of("images/clothes.jpg"),
                        List.of("images/feedClothes.jpg")
                );

                s3Scheduler.deleteImages();

                ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
                verify(s3Client).deleteObjects(captor.capture());

                List<String> deletedKeys = captor.getValue().delete().objects().stream()
                        .map(ObjectIdentifier::key)
                        .toList();
                assertThat(deletedKeys).containsExactly("images/orphan.jpg");
            }

            @Test
            @DisplayName("profileRepository 참조 키는 삭제하지 않는다")
            void preservesProfileKeys() {
                stubBucket();
                stubS3Keys("images/profile-only.jpg", "images/orphan.jpg");
                stubRepositories(
                        Set.of("images/profile-only.jpg"),
                        Set.of(),
                        List.of()
                );

                s3Scheduler.deleteImages();

                ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
                verify(s3Client).deleteObjects(captor.capture());

                List<String> deletedKeys = captor.getValue().delete().objects().stream()
                        .map(ObjectIdentifier::key)
                        .toList();
                assertThat(deletedKeys).doesNotContain("images/profile-only.jpg");
            }

            @Test
            @DisplayName("clothesRepository 참조 키는 삭제하지 않는다")
            void preservesClothesKeys() {
                stubBucket();
                stubS3Keys("images/clothes-only.jpg", "images/orphan.jpg");
                stubRepositories(
                        Set.of(),
                        Set.of("images/clothes-only.jpg"),
                        List.of()
                );

                s3Scheduler.deleteImages();

                ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
                verify(s3Client).deleteObjects(captor.capture());

                List<String> deletedKeys = captor.getValue().delete().objects().stream()
                        .map(ObjectIdentifier::key)
                        .toList();
                assertThat(deletedKeys).doesNotContain("images/clothes-only.jpg");
            }

            @Test
            @DisplayName("feedClothesRepository 참조 키는 삭제하지 않는다")
            void preservesFeedClothesKeys() {
                stubBucket();
                stubS3Keys("images/fc-snapshot.jpg", "images/orphan.jpg");
                stubRepositories(
                        Set.of(),
                        Set.of(),
                        List.of("images/fc-snapshot.jpg")
                );

                s3Scheduler.deleteImages();

                ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
                verify(s3Client).deleteObjects(captor.capture());

                List<String> deletedKeys = captor.getValue().delete().objects().stream()
                        .map(ObjectIdentifier::key)
                        .toList();
                assertThat(deletedKeys).doesNotContain("images/fc-snapshot.jpg");
                assertThat(deletedKeys).containsExactly("images/orphan.jpg");
            }
        }

        @Nested
        @DisplayName("고아 이미지 없음")
        class NoOrphans {

            @Test
            @DisplayName("모든 키가 참조되면 deleteObjects를 호출하지 않는다")
            void allReferencedNoDelete() {
                stubBucket();
                stubS3Keys("images/a.jpg", "images/b.jpg");
                stubRepositories(
                        Set.of("images/a.jpg"),
                        Set.of("images/b.jpg"),
                        List.of()
                );

                s3Scheduler.deleteImages();

                verify(s3Client, never()).deleteObjects(any(DeleteObjectsRequest.class));
            }

            @Test
            @DisplayName("S3 버킷이 비어있으면 deleteObjects를 호출하지 않는다")
            void emptyBucketNoDelete() {
                stubBucket();
                stubS3Keys(); // 빈 배열
                stubRepositories(Set.of(), Set.of(), List.of());

                s3Scheduler.deleteImages();

                verify(s3Client, never()).deleteObjects(any(DeleteObjectsRequest.class));
            }
        }

        @Nested
        @DisplayName("배치 삭제")
        class BatchDeletion {

            @Test
            @DisplayName("1000건 초과 시 나눠서 삭제한다")
            void deletesInBatchesOf1000() {
                stubBucket();

                // 2500개의 고아 키 생성
                String[] keys = IntStream.range(0, 2500)
                        .mapToObj(i -> "images/orphan-" + i + ".jpg")
                        .toArray(String[]::new);
                stubS3Keys(keys);
                stubRepositories(Set.of(), Set.of(), List.of()); // DB 참조 없음 → 전부 고아

                s3Scheduler.deleteImages();

                ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
                verify(s3Client, times(3)).deleteObjects(captor.capture());

                List<DeleteObjectsRequest> requests = captor.getAllValues();
                assertThat(requests.get(0).delete().objects()).hasSize(1000);
                assertThat(requests.get(1).delete().objects()).hasSize(1000);
                assertThat(requests.get(2).delete().objects()).hasSize(500);
            }
        }

        @Nested
        @DisplayName("Repository 호출")
        class RepositoryInteraction {

            @Test
            @DisplayName("feedClothesRepository.findAllImageKeys()가 호출된다")
            void callsFeedClothesRepo() {
                stubBucket();
                stubS3Keys("images/a.jpg");
                stubRepositories(Set.of(), Set.of(), List.of("images/a.jpg"));

                s3Scheduler.deleteImages();

                verify(feedClothesRepository).findAllImageKeys();
            }

            @Test
            @DisplayName("profileRepository.findAllImageKeys()가 호출된다")
            void callsProfileRepo() {
                stubBucket();
                stubS3Keys("images/a.jpg");
                stubRepositories(Set.of("images/a.jpg"), Set.of(), List.of());

                s3Scheduler.deleteImages();

                verify(profileRepository).findAllImageKeys();
            }

            @Test
            @DisplayName("clothesRepository.findAllImageKeys()가 호출된다")
            void callsClothesRepo() {
                stubBucket();
                stubS3Keys("images/a.jpg");
                stubRepositories(Set.of(), Set.of("images/a.jpg"), List.of());

                s3Scheduler.deleteImages();

                verify(clothesRepository).findAllImageKeys();
            }
        }

        @Nested
        @DisplayName("S3 페이지네이션")
        class Pagination {

            @Test
            @DisplayName("isTruncated 응답 시 다음 페이지를 조회한다")
            void handlesTruncatedResponse() {
                stubBucket();
                stubRepositories(Set.of(), Set.of(), List.of());

                // 첫 번째 페이지: truncated
                ListObjectsV2Response page1 = ListObjectsV2Response.builder()
                        .contents(List.of(S3Object.builder().key("images/page1.jpg").build()))
                        .isTruncated(true)
                        .nextContinuationToken("token-abc")
                        .build();
                // 두 번째 페이지: 마지막
                ListObjectsV2Response page2 = ListObjectsV2Response.builder()
                        .contents(List.of(S3Object.builder().key("images/page2.jpg").build()))
                        .isTruncated(false)
                        .build();

                when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                        .thenReturn(page1, page2);

                s3Scheduler.deleteImages();

                // listObjectsV2가 2번 호출되었는지 검증
                verify(s3Client, times(2)).listObjectsV2(any(ListObjectsV2Request.class));

                // 두 페이지 모두 고아이므로 2개가 삭제 대상
                ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
                verify(s3Client).deleteObjects(captor.capture());

                List<String> deletedKeys = captor.getValue().delete().objects().stream()
                        .map(ObjectIdentifier::key)
                        .toList();
                assertThat(deletedKeys).containsExactlyInAnyOrder("images/page1.jpg", "images/page2.jpg");
            }
        }
    }
}
