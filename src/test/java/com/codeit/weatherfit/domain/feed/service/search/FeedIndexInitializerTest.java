package com.codeit.weatherfit.domain.feed.service.search;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.search.FeedDocument;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.repository.search.FeedSearchRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedIndexInitializerTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private FeedLikeRepository feedLikeRepository;

    @Mock
    private FeedSearchRepository feedSearchRepository;

    @InjectMocks
    private FeedIndexInitializer feedIndexInitializer;

    @Test
    @DisplayName("ES가 비어있고 DB에 피드가 있으면 전체 인덱싱한다")
    void indexesAllFeedsWhenEsIsEmpty() {
        // given
        List<Feed> feeds = Instancio.ofList(Feed.class).size(3).create();
        when(feedSearchRepository.count()).thenReturn(0L);
        when(feedRepository.findAll()).thenReturn(feeds);
        when(feedLikeRepository.countByFeed(any(Feed.class))).thenReturn(0L);

        // when
        feedIndexInitializer.run(null);

        // then
        verify(feedSearchRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("ES에 이미 문서가 있으면 인덱싱을 스킵한다")
    void skipsWhenEsHasDocuments() {
        // given
        when(feedSearchRepository.count()).thenReturn(10L);

        // when
        feedIndexInitializer.run(null);

        // then
        verify(feedRepository, never()).findAll();
        verify(feedSearchRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("ES가 비어있어도 DB에 피드가 없으면 스킵한다")
    void skipsWhenDbHasNoFeeds() {
        // given
        when(feedSearchRepository.count()).thenReturn(0L);
        when(feedRepository.findAll()).thenReturn(List.of());

        // when
        feedIndexInitializer.run(null);

        // then
        verify(feedSearchRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("DB의 모든 피드 수만큼 FeedDocument가 생성된다")
    void createsDocumentForEachFeed() {
        // given
        int feedCount = 5;
        List<Feed> feeds = Instancio.ofList(Feed.class).size(feedCount).create();
        when(feedSearchRepository.count()).thenReturn(0L);
        when(feedRepository.findAll()).thenReturn(feeds);
        when(feedLikeRepository.countByFeed(any(Feed.class))).thenReturn(0L);

        // when
        feedIndexInitializer.run(null);

        // then
        @SuppressWarnings("unchecked")
        var captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(feedSearchRepository).saveAll(captor.capture());
        List<?> savedDocs = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(savedDocs).hasSize(feedCount);
    }
}
