package com.codeit.weatherfit.global.s3;

import com.codeit.weatherfit.global.s3.exception.S3StorageUploadException;
import com.codeit.weatherfit.global.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
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
    public String put(byte[] bytes,String fileName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(fileName)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(bytes)
        );
        return fileName;
    }

    @Override
    public String put(MultipartFile file) {
        String key = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .key(key)
                            .bucket(s3Properties.bucket())
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (SdkClientException | IOException e) {
            log.warn("파일 업로드에 실패함 : {}", key);
            throw new S3StorageUploadException(key);
        }
        log.info("S3 파일 업로드 완료 : {}", key);
        return key;
    }

    @Override
    public String delete(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(fileName)
                .build();
        s3Client.deleteObject(
                request
        );
        return fileName;
    }

    @Override
    public String getUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(s3Properties.presignedUrlExpirationTime()))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(s3Properties.bucket())
                        .key(key)
                        .build())
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
