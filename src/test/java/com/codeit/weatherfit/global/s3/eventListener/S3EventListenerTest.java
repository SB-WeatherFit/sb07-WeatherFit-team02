package com.codeit.weatherfit.global.s3.eventListener;

import com.codeit.weatherfit.domain.clothes.service.ClothesService;
import com.codeit.weatherfit.domain.profile.service.ProfileService;
import com.codeit.weatherfit.global.s3.S3Service;
import com.codeit.weatherfit.global.s3.event.S3ClothesPutEvent;
import com.codeit.weatherfit.global.s3.event.S3ProfilePutEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class S3EventListenerTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ClothesService clothesService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private S3EventListener s3EventListener;

    @Nested
    @DisplayName("Clothes 이미지 업로드")
    class ClothesPut {

        @Test
        @DisplayName("성공 - S3에 파일을 업로드한다")
        void success() {
            // given
            S3ClothesPutEvent event = new S3ClothesPutEvent(
                    UUID.randomUUID(), "1711234567_photo.jpg", "image/jpeg", "image-data".getBytes()
            );

            // when
            s3EventListener.handleS3PutEvent(event);

            // then
            verify(s3Service).put(event.fileName(), event.contentType(), event.bytes());
        }

        @Test
        @DisplayName("실패 - S3 예외가 발생하면 그대로 던진다")
        void throwsOnFailure() {
            // given
            S3ClothesPutEvent event = new S3ClothesPutEvent(
                    UUID.randomUUID(), "1711234567_photo.jpg", "image/jpeg", "image-data".getBytes()
            );
            doThrow(SdkClientException.create("connection failed"))
                    .when(s3Service).put(event.fileName(), event.contentType(), event.bytes());

            // when & then
            assertThatThrownBy(() -> s3EventListener.handleS3PutEvent(event))
                    .isInstanceOf(SdkClientException.class);
        }
    }

    @Nested
    @DisplayName("Clothes 업로드 recover")
    class ClothesRecover {

        @Test
        @DisplayName("최종 실패 시 clothesService.clearImageKey를 호출한다")
        void clearsImageKey() {
            // given
            UUID clothesId = UUID.randomUUID();
            S3ClothesPutEvent event = new S3ClothesPutEvent(
                    clothesId, "1711234567_photo.jpg", "image/jpeg", "image-data".getBytes()
            );

            // when
            s3EventListener.recover(new RuntimeException("final failure"), event);

            // then
            verify(clothesService).clearImageKey(clothesId);
            verifyNoInteractions(profileService);
        }
    }

    @Nested
    @DisplayName("Profile 이미지 업로드")
    class ProfilePut {

        @Test
        @DisplayName("성공 - S3에 파일을 업로드한다")
        void success() {
            // given
            S3ProfilePutEvent event = new S3ProfilePutEvent(
                    UUID.randomUUID(), "1711234567_profile.jpg", "image/png", "profile-data".getBytes()
            );

            // when
            s3EventListener.handleS3PutEvent(event);

            // then
            verify(s3Service).put(event.fileName(), event.contentType(), event.bytes());
        }

        @Test
        @DisplayName("실패 - S3 예외가 발생하면 그대로 던진다")
        void throwsOnFailure() {
            // given
            S3ProfilePutEvent event = new S3ProfilePutEvent(
                    UUID.randomUUID(), "1711234567_profile.jpg", "image/png", "profile-data".getBytes()
            );
            doThrow(SdkClientException.create("connection failed"))
                    .when(s3Service).put(event.fileName(), event.contentType(), event.bytes());

            // when & then
            assertThatThrownBy(() -> s3EventListener.handleS3PutEvent(event))
                    .isInstanceOf(SdkClientException.class);
        }
    }

    @Nested
    @DisplayName("Profile 업로드 recover")
    class ProfileRecover {

        @Test
        @DisplayName("최종 실패 시 profileService.clearImageKey를 호출한다")
        void clearsImageKey() {
            // given
            UUID profileId = UUID.randomUUID();
            S3ProfilePutEvent event = new S3ProfilePutEvent(
                    profileId, "1711234567_profile.jpg", "image/png", "profile-data".getBytes()
            );

            // when
            s3EventListener.recover(new RuntimeException("final failure"), event);

            // then
            verify(profileService).clearImageKey(profileId);
            verifyNoInteractions(clothesService);
        }
    }
}
