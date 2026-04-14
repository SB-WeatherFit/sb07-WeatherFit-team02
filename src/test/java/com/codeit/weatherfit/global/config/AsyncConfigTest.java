package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.domain.feed.repository.search.FeedSearchRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AsyncConfigTest {

    @Autowired
    private SimpleAsyncService asyncTestService;
    @MockitoBean
    private FeedSearchRepository feedSearchRepository;

    @Test
    void async() throws Exception {
        String mainThreadName = Thread.currentThread().getName();

        CompletableFuture<String> future = asyncTestService.getThreadName();

        String asyncThreadName = future.get(3, TimeUnit.SECONDS);
        System.out.println("asyncThreadName = " + asyncThreadName);


        assertThat(asyncThreadName).isNotEqualTo(mainThreadName);
        assertThat(asyncThreadName).startsWith("message-");
    }

    @Test
    void mdc() throws Exception {
        String expectedId = "test-log-id-1234";
        MDC.put("requestId", expectedId);

        try {
            CompletableFuture<String> future = asyncTestService.getMdcValue();

            String actualId = future.get(3, TimeUnit.SECONDS);

            System.out.println("메인 스레드 MDC ID: " + expectedId);
            System.out.println("비동기 스레드 MDC ID: " + actualId);

            assertThat(actualId).isEqualTo(expectedId);
        } finally {
            MDC.clear();
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SimpleAsyncService simpleAsyncService() {
            return new SimpleAsyncService();
        }
    }

    static class SimpleAsyncService {
        @Async("messageTaskExecutor")
        public CompletableFuture<String> getThreadName() {
            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }

        @Async("messageTaskExecutor")
        public CompletableFuture<String> getMdcValue() {
            return CompletableFuture.completedFuture(MDC.get("requestId"));
        }
    }
}