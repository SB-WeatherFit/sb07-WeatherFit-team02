package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, MessageCreatedEvent> messageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 컨슈머 그룹 ID
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "message-send-group");

//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 역직렬화 설정
        // JSON을 MessageCreatedEvent 객체로 변환하기 위한 설정입니다.
        JsonDeserializer<MessageCreatedEvent> deserializer = new JsonDeserializer<>(MessageCreatedEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*"); // 모든 패키지의 객체 변환 허용


        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(), // Key 역직렬화 도구
                new ErrorHandlingDeserializer<>(deserializer) // Value 역직렬화 도구 + 에러 방어막
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> messageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(messageConsumerFactory());
        return factory;
    }
}