package com.codeit.weatherfit.domain.feed.service.search;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.search.FeedDocument;
import com.codeit.weatherfit.domain.feed.repository.FeedLikeRepository;
import com.codeit.weatherfit.domain.feed.repository.FeedRepository;
import com.codeit.weatherfit.domain.feed.repository.search.FeedSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedIndexInitializer implements ApplicationRunner {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedSearchRepository feedSearchRepository;

    @Override
    public void run(ApplicationArguments args) {
        long esCount = feedSearchRepository.count();
        if (esCount > 0) {
            log.info("[ES 초기화] 이미 {}건의 문서가 존재하여 스킵합니다.", esCount);
            return;
        }

        List<Feed> feeds = feedRepository.findAll();
        if (feeds.isEmpty()) {
            log.info("[ES 초기화] DB에 피드가 없어 스킵합니다.");
            return;
        }

        log.info("[ES 초기화] DB 피드 {}건을 ES에 인덱싱합니다.", feeds.size());
        List<FeedDocument> docs = feeds.stream()
                .map(feed -> FeedDocument.from(feed, feedLikeRepository.countByFeed(feed)))
                .toList();
        feedSearchRepository.saveAll(docs);
        log.info("[ES 초기화] 인덱싱 완료: {}건", docs.size());
    }
}
