package com.codeit.weatherfit.global.util.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name="weatherfit.storage.type",havingValue = "s3")
public class S3ServiceImpl implements S3Service {
    private final S3Property s3Property;
    private S3Client s3Client;
    private S3Presigner s3Presigner;

    private String accesskey;
    private String secretkey;
    private String region;
    private String bucket;
    private int expirationSeconds;

    @PostConstruct
    private void initialize(){
        this.accesskey = s3Property.getS3AccessKey();
        this.secretkey = s3Property.getS3SecretKey();
        this.region = s3Property.getS3Region();
        this.bucket = s3Property.getS3Bucket();
        this.expirationSeconds = s3Property.getS3PresignedUrlExpiration();

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accesskey, secretkey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }


    @Override
    public String put(byte[] bytes,String fileName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(bytes)
        );
        return urlResolver(fileName);
    }

    @Override
    public String delete(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();
        s3Client.deleteObject(
                request
        );
        return fileName;
    }

    @Override
    public byte[] get(String fileName) {
        return new byte[0];
    }

    private String urlResolver(String fileName){
        return s3Client.utilities()
                .getUrl(b -> b.bucket(bucket).key(fileName))
                .toExternalForm();
    }
}
