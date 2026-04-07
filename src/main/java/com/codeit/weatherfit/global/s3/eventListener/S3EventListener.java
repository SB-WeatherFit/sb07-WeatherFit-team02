package com.codeit.weatherfit.global.s3.eventListener;

import com.codeit.weatherfit.domain.clothes.service.ClothesService;
import com.codeit.weatherfit.domain.profile.service.ProfileService;
import com.codeit.weatherfit.global.s3.S3Service;
import com.codeit.weatherfit.global.s3.event.S3ClothesDeletedEvent;
import com.codeit.weatherfit.global.s3.event.S3ClothesPutEvent;
import com.codeit.weatherfit.global.s3.event.S3ProfileDeletedEvent;
import com.codeit.weatherfit.global.s3.event.S3ProfilePutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3EventListener {
    private final S3Service s3Service;
    private final ClothesService clothesService;
    private final ProfileService profileService;

    @Async("s3TaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void handleS3PutEvent(S3ClothesPutEvent event) {
        s3Service.put(event.fileName(), event.contentType(), event.bytes());
    }

    @Recover
    public void recover(Exception e, S3ClothesPutEvent event) {
        log.error("S3 업로드 최종 실패: key={}", event.fileName(), e);
        clothesService.clearImageKey(event.clothesId());
    }

    @Async("s3TaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void handleS3PutEvent(S3ProfilePutEvent event) {
        s3Service.put(event.fileName(), event.contentType(), event.bytes());
    }

    @Recover
    public void recover(Exception e, S3ProfilePutEvent event) {
        log.error("S3 업로드 최종 실패: key={}", event.fileName(), e);
        profileService.clearImageKey(event.userId());
    }

    @Async("s3TaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void handleProfileDeleteEvent(S3ProfileDeletedEvent event) {
        s3Service.delete(event.imageKey());
    }

    @Recover
    public void recover(Exception e, S3ProfileDeletedEvent event) {
        log.error("S3 삭제 최종 실패: key={}", event.imageKey(), e);
    }

    @Async("s3TaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void handleClothesDeleteEvent(S3ClothesDeletedEvent event) {
        s3Service.delete(event.imageKey());
    }

    @Recover
    public void recover(Exception e, S3ClothesDeletedEvent event) {
        log.error("S3 업로드 최종 실패: key={}", event.imageKey(), e);
    }

}
