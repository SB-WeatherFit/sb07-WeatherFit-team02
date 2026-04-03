package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.domain.notification.service.redis.RedisBroadcastSubscriber;
import com.codeit.weatherfit.domain.notification.service.redis.RedisPersonalSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisNotificationConfig {

    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            MessageListenerAdapter personalListenerAdapter,
            MessageListenerAdapter broadcastListenerAdapter) {
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // 1. 개별 알림 채널 구독 (Topic: notification.personal)
        container.addMessageListener(personalListenerAdapter, new ChannelTopic("notification.personal"));
        
        // 2. 시스템 브로드캐스트 채널 구독 (Topic: notification.broadcast)
        container.addMessageListener(broadcastListenerAdapter, new ChannelTopic("notification.broadcast"));
        
        return container;
    }

    // 개별 알림 리스너 어댑터 설정
    @Bean
    public MessageListenerAdapter personalListenerAdapter(RedisPersonalSubscriber subscriber) {
        // 실제 로직이 담긴 subscriber 클래스를 연결
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    // 시스템 브로드캐스트 리스너 어댑터 설정
    @Bean
    public MessageListenerAdapter broadcastListenerAdapter(RedisBroadcastSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}