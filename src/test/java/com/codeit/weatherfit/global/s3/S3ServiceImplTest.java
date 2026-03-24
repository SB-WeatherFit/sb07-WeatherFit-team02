package com.codeit.weatherfit.global.s3;

import com.codeit.weatherfit.global.s3.exception.S3StorageUploadException;
import com.codeit.weatherfit.global.s3.properties.S3Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Nested
    @DisplayName("put(MultipartFile)")
    class PutMultipartFile {

        @Test
        @DisplayName("성공 - 파일을 업로드하고 key를 반환한다")
        void success() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "photo.jpg", "image/jpeg", "test-image".getBytes()
            );
            when(s3Properties.bucket()).thenReturn("test-bucket");
            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            // when
            String key = s3Service.put(file);

            // then
            assertThat(key).endsWith("-photo.jpg");
            ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
            verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
            assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
            assertThat(captor.getValue().contentType()).isEqualTo("image/jpeg");
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("S3 클라이언트 예외 시 S3StorageUploadException을 던진다")
            void sdkClientException() {
                // given
                MockMultipartFile file = new MockMultipartFile(
                        "file", "photo.jpg", "image/jpeg", "test-image".getBytes()
                );
                when(s3Properties.bucket()).thenReturn("test-bucket");
                when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                        .thenThrow(SdkClientException.create("connection failed"));

                // when & then
                assertThatThrownBy(() -> s3Service.put(file))
                        .isInstanceOf(S3StorageUploadException.class);
            }
        }
    }

    @Nested
    @DisplayName("put(byte[], String)")
    class PutBytes {

        @Test
        @DisplayName("성공 - 바이트 배열을 업로드하고 fileName을 반환한다")
        void success() {
            // given
            byte[] bytes = "log-content".getBytes();
            String fileName = "logs/app-2026-03-23.log.gz";
            when(s3Properties.bucket()).thenReturn("test-bucket");
            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            // when
            String result = s3Service.put(bytes, fileName);

            // then
            assertThat(result).isEqualTo(fileName);
            ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
            verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
            assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
            assertThat(captor.getValue().key()).isEqualTo(fileName);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("성공 - 파일을 삭제하고 fileName을 반환한다")
        void success() {
            // given
            String fileName = "photo.jpg";
            when(s3Properties.bucket()).thenReturn("test-bucket");

            // when
            String result = s3Service.delete(fileName);

            // then
            assertThat(result).isEqualTo(fileName);
            ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
            verify(s3Client).deleteObject(captor.capture());
            assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
            assertThat(captor.getValue().key()).isEqualTo(fileName);
        }
    }

    @Nested
    @DisplayName("getUrl")
    class GetUrl {

        @Test
        @DisplayName("성공 - presigned URL을 생성하여 반환한다")
        void success() throws Exception {
            // given
            String key = "photo.jpg";
            when(s3Properties.bucket()).thenReturn("test-bucket");
            when(s3Properties.presignedUrlExpirationTime()).thenReturn(600L);

            PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
            when(presignedRequest.url()).thenReturn(
                    URI.create("https://test-bucket.s3.ap-northeast-2.amazonaws.com/photo.jpg?X-Amz-Signature=abc").toURL()
            );
            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenReturn(presignedRequest);

            // when
            String url = s3Service.getUrl(key);

            // then
            assertThat(url).contains("test-bucket").contains("photo.jpg");
            verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
        }
    }
}