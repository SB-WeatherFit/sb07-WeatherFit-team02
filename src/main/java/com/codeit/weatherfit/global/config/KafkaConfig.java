package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }


    @Bean
    public ConsumerFactory<String, MessageCreatedEvent> messageConsumerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

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
    public ProducerFactory<String, MessageCreatedEvent> messageProducerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //  키 직렬화: StringSerializer
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        //  값 직렬화: JsonSerializer
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, MessageCreatedEvent> messageKafkaTemplate() {
        // 실제 서비스에서 주입받아 사용할 '메시지 발송 도구'입니다.
        return new KafkaTemplate<>(messageProducerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> messageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(messageConsumerFactory());
        return factory;
    }
}