package com.codeit.weatherfit.domain.feed.eventListener;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.search.FeedDocument;
import com.codeit.weatherfit.domain.feed.event.FeedCreatedEvent;
import com.codeit.weatherfit.domain.feed.event.FeedDeletedEvent;
import com.codeit.weatherfit.domain.feed.event.FeedUpdatedEvent;
import com.codeit.weatherfit.domain.feed.exception.FeedDocumentNotFoundException;
import com.codeit.weatherfit.domain.feed.exception.FeedNotExistException;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.repository.search.FeedSearchRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EsEventListenerTest {

    @Mock
    private FeedSearchRepository feedSearchRepository;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private FeedLikeRepository feedLikeRepository;

    @InjectMocks
    private EsEventListener esEventListener;

    @Nested
    @DisplayName("피드 생성 이벤트")
    class HandleFeedCreatedEvent {

        @Test
        @DisplayName("DB에서 피드를 조회하고 ES에 인덱싱한다")
        void indexesFeedToEs() {
            // given
            Feed feed = Instancio.create(Feed.class);
            UUID feedId = feed.getId();
            FeedCreatedEvent event = new FeedCreatedEvent(feedId);

            when(feedRepository.findById(feedId)).thenReturn(Optional.of(feed));
            when(feedLikeRepository.countByFeed(feed)).thenReturn(5L);

            // when
            esEventListener.handleFeedCreatedEvent(event);

            // then
            verify(feedSearchRepository).save(any(FeedDocument.class));
        }

        @Test
        @DisplayName("DB에 피드가 없으면 FeedNotExistException을 던진다")
        void throwsWhenFeedNotFound() {
            // given
            UUID feedId = UUID.randomUUID();
            FeedCreatedEvent event = new FeedCreatedEvent(feedId);

            when(feedRepository.findById(feedId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> esEventListener.handleFeedCreatedEvent(event))
                    .isInstanceOf(FeedNotExistException.class);
            verify(feedSearchRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("피드 수정 이벤트")
    class HandleFeedUpdatedEvent {

        @Test
        @DisplayName("LIKE_UP 이벤트를 받으면 ES 문서의 likeCount를 증가시킨다")
        void incrementsLikeCount() {
            // given
            UUID feedId = UUID.randomUUID();
            FeedUpdatedEvent event = FeedUpdatedEvent.liked(feedId);
            FeedDocument doc = createDocument(feedId, "내용", 10);

            when(feedSearchRepository.findById(feedId)).thenReturn(Optional.of(doc));

            // when
            esEventListener.handleFeedUpdatedEvent(event);

            // then
            verify(feedSearchRepository).save(doc);
            // liked()가 호출되었으므로 likeCount가 11이어야 한다
            assertThatLikeCount(doc, 11);
        }

        @Test
        @DisplayName("LIKE_DOWN 이벤트를 받으면 ES 문서의 likeCount를 감소시킨다")
        void decrementsLikeCount() {
            // given
            UUID feedId = UUID.randomUUID();
            FeedUpdatedEvent event = FeedUpdatedEvent.unliked(feedId);
            FeedDocument doc = createDocument(feedId, "내용", 10);

            when(feedSearchRepository.findById(feedId)).thenReturn(Optional.of(doc));

            // when
            esEventListener.handleFeedUpdatedEvent(event);

            // then
            verify(feedSearchRepository).save(doc);
            assertThatLikeCount(doc, 9);
        }

        @Test
        @DisplayName("CONTENT_UPDATED 이벤트를 받으면 ES 문서의 content를 변경한다")
        void updatesContent() {
            // given
            UUID feedId = UUID.randomUUID();
            String newContent = "수정된 내용";
            FeedUpdatedEvent event = FeedUpdatedEvent.contentUpdated(feedId, newContent);
            FeedDocument doc = createDocument(feedId, "원래 내용", 0);

            when(feedSearchRepository.findById(feedId)).thenReturn(Optional.of(doc));

            // when
            esEventListener.handleFeedUpdatedEvent(event);

            // then
            verify(feedSearchRepository).save(doc);
            // updateContent()가 호출되었으므로 content가 변경되어야 한다
            // FeedDocument의 content 필드를 리플렉션으로 확인
            try {
                var field = FeedDocument.class.getDeclaredField("content");
                field.setAccessible(true);
                assertThat(field.get(doc)).isEqualTo(newContent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        @DisplayName("ES에 문서가 없으면 FeedDocumentNotFoundException을 던진다")
        void throwsWhenDocumentNotFound() {
            // given
            UUID feedId = UUID.randomUUID();
            FeedUpdatedEvent event = FeedUpdatedEvent.liked(feedId);

            when(feedSearchRepository.findById(feedId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> esEventListener.handleFeedUpdatedEvent(event))
                    .isInstanceOf(FeedDocumentNotFoundException.class);
            verify(feedSearchRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("피드 삭제 이벤트")
    class HandleFeedDeletedEvent {

        @Test
        @DisplayName("feedId로 ES 문서를 삭제한다")
        void deletesDocument() {
            // given
            UUID feedId = UUID.randomUUID();
            FeedDeletedEvent event = new FeedDeletedEvent(feedId);

            // when
            esEventListener.handleFeedDeletedEvent(event);

            // then
            verify(feedSearchRepository).deleteByFeedId(feedId);
        }
    }

    private FeedDocument createDocument(UUID feedId, String content, long likeCount) {
        FeedDocument doc = new FeedDocument();
        try {
            setField(doc, "feedId", feedId);
            setField(doc, "content", content);
            setField(doc, "likeCount", likeCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void assertThatLikeCount(FeedDocument doc, long expected) {
        try {
            var field = FeedDocument.class.getDeclaredField("likeCount");
            field.setAccessible(true);
            assertThat(field.get(doc)).isEqualTo(expected);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
