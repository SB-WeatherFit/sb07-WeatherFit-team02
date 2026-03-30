package com.codeit.weatherfit.global.s3;

import com.codeit.weatherfit.global.s3.exception.S3DeleteException;
import com.codeit.weatherfit.global.s3.exception.S3UploadException;
import com.codeit.weatherfit.global.s3.exception.S3UrlException;
import com.codeit.weatherfit.global.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name="weatherfit.storage.type", havingValue = "s3")
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final S3Client s3Client;


    @Override
    public String put(byte[] bytes, String fileName) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(fileName)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (SdkClientException e) {
            log.warn("파일 업로드에 실패함 : {}", fileName);
            throw new S3UploadException(fileName);
        }
        log.info("S3 파일 업로드 완료 : {}", fileName);
        return fileName;
    }

    @Override
    public String put(String fileName, String contentType, byte[] bytes) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .key(fileName)
                            .bucket(s3Properties.bucket())
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
        } catch (SdkClientException e) {
            log.warn("파일 업로드에 실패함 : {}", fileName);
            throw new S3UploadException(fileName);
        }
        log.info("S3 파일 업로드 완료 : {}", fileName);
        return fileName;
    }

    @Override
    public String delete(String fileName) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(fileName)
                    .build();
            s3Client.deleteObject(request);
        } catch (SdkClientException e) {
            log.warn("파일 삭제에 실패함 : {}", fileName);
            throw new S3DeleteException(fileName);
        }
        log.info("S3 파일 삭제 완료 : {}", fileName);
        return fileName;
    }

    @Override
    public String getUrl(String key) {
        if (key == null)
            return null;
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(s3Properties.presignedUrlExpirationTime()))
                    .getObjectRequest(GetObjectRequest.builder()
                            .bucket(s3Properties.bucket())
                            .key(key)
                            .build())
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (SdkClientException e) {
            log.warn("파일 URL 생성에 실패함 : {}", key);
            throw new S3UrlException(key);
        }
    }
}