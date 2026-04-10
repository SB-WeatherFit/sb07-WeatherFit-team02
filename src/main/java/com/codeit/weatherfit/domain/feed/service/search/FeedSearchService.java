package com.codeit.weatherfit.domain.feed.service.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.codeit.weatherfit.domain.feed.dto.request.FeedGetRequest;
import com.codeit.weatherfit.domain.feed.dto.request.SortBy;
import com.codeit.weatherfit.domain.feed.dto.request.SortDirection;
import com.codeit.weatherfit.domain.feed.entity.search.FeedDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedSearchService {

    private final ElasticsearchOperations operations;

    public List<UUID> searchFeeds(FeedGetRequest request) {
        log.info("ES 검색 요청: keyword={}, skyStatus={}, precipitationType={}, authorId={}, sortBy={}, limit={}",
                request.keywordLike(), request.skyStatusEqual(), request.precipitationTypeEqual(),
                request.authorIdEqual(), request.sortBy(), request.limit());
        NativeQuery query = buildSearchQuery(request);
        log.debug("ES 쿼리: {}", query.getQuery());
        SearchHits<FeedDocument> hits = operations.search(query, FeedDocument.class);
        List<UUID> result = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getFeedId())
                .toList();
        log.info("ES 검색 결과: totalHits={}, returnedIds={}", hits.getTotalHits(), result);
        return result;
    }

    private NativeQuery buildSearchQuery(FeedGetRequest request) {
        BoolQuery.Builder bool = new BoolQuery.Builder();

        // 키워드 검색 - must (점수 계산 O)
        if (request.keywordLike() != null) {
            bool.must(m -> m.match(
                    mt -> mt.field("content").query(request.keywordLike())
            ));
        }

        // 필터 조건들 - filter (점수 계산 X)
        if (request.skyStatusEqual() != null) {
            bool.filter(f -> f.term(
                    t -> t.field("skyStatus").value(request.skyStatusEqual().name())
            ));
        }
        if (request.precipitationTypeEqual() != null) {
            bool.filter(f -> f.term(
                    t -> t.field("skyStatus").value(request.precipitationTypeEqual().name()))
            );
        }
        if (request.authorIdEqual() != null) {
            bool.filter(f -> f.term(
                    t -> t.field("authorId").value(request.authorIdEqual().toString())
            ));
        }

        NativeQueryBuilder builder = NativeQuery.builder()
                .withQuery(q -> q.bool(bool.build()))
                .withSort(buildSort(request))
                .withPageable(PageRequest.of(0, request.limit() + 1));

        if (request.cursor() != null) {
            builder.withSearchAfter(List.of(request.cursor()));
        }

        return builder.build();
    }

    private Sort buildSort(FeedGetRequest request) {
        String filed = request.sortBy() == SortBy.likeCount
                ? "likeCount" : "createdAt";
        Sort.Direction direction = request.sortDirection() == SortDirection.ASCENDING
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, filed);
    }
}
