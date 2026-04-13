package com.codeit.weatherfit.domain.feed.service.search;

import com.codeit.weatherfit.domain.feed.dto.request.FeedGetRequest;
import com.codeit.weatherfit.domain.feed.dto.request.SortBy;
import com.codeit.weatherfit.domain.feed.dto.request.SortDirection;
import com.codeit.weatherfit.domain.feed.entity.search.FeedDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedSearchServiceTest {

    @Mock
    private ElasticsearchOperations operations;

    @InjectMocks
    private FeedSearchService feedSearchService;

    @Nested
    @DisplayName("searchFeeds")
    class SearchFeeds {

        @Test
        @DisplayName("키워드 검색 시 매칭된 피드 ID 목록을 반환한다")
        void returnsFeedIds() {
            // given
            UUID feedId1 = UUID.randomUUID();
            UUID feedId2 = UUID.randomUUID();
            FeedGetRequest request = new FeedGetRequest(
                    null, null, 10, SortBy.createdAt, SortDirection.DESCENDING,
                    "반팔", null, null, null
            );

            SearchHits<FeedDocument> hits = mockSearchHits(
                    createFeedDocument(feedId1, "반팔티 입었다"),
                    createFeedDocument(feedId2, "반팔 셔츠 추천")
            );
            when(operations.search(any(NativeQuery.class), eq(FeedDocument.class)))
                    .thenReturn(hits);

            // when
            List<UUID> result = feedSearchService.searchFeeds(request);

            // then
            assertThat(result).containsExactly(feedId1, feedId2);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환한다")
        void returnsEmptyListWhenNoResults() {
            // given
            FeedGetRequest request = new FeedGetRequest(
                    null, null, 10, SortBy.createdAt, SortDirection.DESCENDING,
                    "존재하지않는키워드", null, null, null
            );

            SearchHits<FeedDocument> emptyHits = mockSearchHits();
            when(operations.search(any(NativeQuery.class), eq(FeedDocument.class)))
                    .thenReturn(emptyHits);

            // when
            List<UUID> result = feedSearchService.searchFeeds(request);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit + 1건을 요청하여 hasNext 판단이 가능하도록 한다")
        void requestsLimitPlusOne() {
            // given
            int limit = 5;
            FeedGetRequest request = new FeedGetRequest(
                    null, null, limit, SortBy.createdAt, SortDirection.DESCENDING,
                    "반팔", null, null, null
            );

            SearchHits<FeedDocument> hits = mockSearchHits();
            when(operations.search(any(NativeQuery.class), eq(FeedDocument.class)))
                    .thenReturn(hits);

            // when
            feedSearchService.searchFeeds(request);

            // then
            ArgumentCaptor<NativeQuery> captor = ArgumentCaptor.forClass(NativeQuery.class);
            verify(operations).search(captor.capture(), eq(FeedDocument.class));
            NativeQuery captured = captor.getValue();
            assertThat(captured.getPageable().getPageSize()).isEqualTo(limit + 1);
        }

        @Test
        @DisplayName("ES 검색 결과의 순서가 보존된다")
        void preservesOrder() {
            // given
            UUID first = UUID.randomUUID();
            UUID second = UUID.randomUUID();
            UUID third = UUID.randomUUID();
            FeedGetRequest request = new FeedGetRequest(
                    null, null, 10, SortBy.likeCount, SortDirection.DESCENDING,
                    "날씨", null, null, null
            );

            SearchHits<FeedDocument> hits = mockSearchHits(
                    createFeedDocument(first, "날씨 좋다"),
                    createFeedDocument(second, "날씨 맑음"),
                    createFeedDocument(third, "날씨 흐림")
            );
            when(operations.search(any(NativeQuery.class), eq(FeedDocument.class)))
                    .thenReturn(hits);

            // when
            List<UUID> result = feedSearchService.searchFeeds(request);

            // then
            assertThat(result).containsExactly(first, second, third);
        }
    }

    private FeedDocument createFeedDocument(UUID feedId, String content) {
        FeedDocument doc = new FeedDocument();
        try {
            var feedIdField = FeedDocument.class.getDeclaredField("feedId");
            feedIdField.setAccessible(true);
            feedIdField.set(doc, feedId);

            var contentField = FeedDocument.class.getDeclaredField("content");
            contentField.setAccessible(true);
            contentField.set(doc, content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    @SuppressWarnings("unchecked")
    private SearchHits<FeedDocument> mockSearchHits(FeedDocument... docs) {
        List<SearchHit<FeedDocument>> hitList = List.of(docs).stream()
                .map(doc -> {
                    SearchHit<FeedDocument> hit = mock(SearchHit.class);
                    when(hit.getContent()).thenReturn(doc);
                    return hit;
                })
                .toList();

        SearchHits<FeedDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(hitList);
        when(searchHits.getTotalHits()).thenReturn((long) docs.length);
        return searchHits;
    }
}
