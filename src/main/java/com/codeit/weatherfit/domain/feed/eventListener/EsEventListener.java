package com.codeit.weatherfit.domain.feed.eventListener;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.search.FeedDocument;
import com.codeit.weatherfit.domain.feed.event.FeedCreatedEvent;
import com.codeit.weatherfit.domain.feed.event.FeedUpdatedEvent;
import com.codeit.weatherfit.domain.feed.exception.FeedDocumentNotFoundException;
import com.codeit.weatherfit.domain.feed.exception.FeedNotExistException;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.repository.search.FeedSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EsEventListener {

    private final FeedSearchRepository feedSearchRepository;
    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;

    @Async("elasticTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxAttempts = 5)
    public void handleFeedCreatedEvent(FeedCreatedEvent event) {
        log.info("[ES 동기화] 피드 생성 이벤트 수신: feedId={}", event.feedId());
        Feed feed = feedRepository.findById(event.feedId())
                .orElseThrow(() -> {
                    log.error("[ES 동기화] 피드 조회 실패: feedId={}", event.feedId());
                    return new FeedNotExistException(event.feedId());
                });
        Long likeCount = feedLikeRepository.countByFeed(feed);
        FeedDocument doc = FeedDocument.from(feed, likeCount);
        feedSearchRepository.save(doc);
        log.info("[ES 동기화] 피드 인덱싱 완료: feedId={}, likeCount={}", event.feedId(), likeCount);
    }

    @Async("elasticTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxAttempts = 5)
    public void handleFeedUpdatedEvent(FeedUpdatedEvent event) {
        log.info("[ES 동기화] 피드 업데이트 이벤트 수신: feedId={}, eventType={}", event.feedId(), event.eventType());
        FeedDocument feedDocument = feedSearchRepository.findById(event.feedId().toString())
                .orElseThrow(() -> {
                    log.error("[ES 동기화] FeedDocument 조회 실패: feedId={}", event.feedId());
                    return new FeedDocumentNotFoundException(event.feedId().toString());
                });
        switch (event.eventType()){
            case LIKE_UP -> feedDocument.liked();
            case LIKE_DOWN -> feedDocument.unliked();
            case CONTENT_UPDATED -> feedDocument.updateContent(event.content());
        }
        feedSearchRepository.save(feedDocument);
        log.info("[ES 동기화] 피드 업데이트 인덱싱 완료: feedId={}, eventType={}", event.feedId(), event.eventType());
    }


}
