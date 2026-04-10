package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String saslJaasConfig;

    private final String CLIENT_DNS = "use_all_dns_ips";
    private final String SECURITY_PROTOCOL = "SASL_SSL";
    private final String SASL_MECHANISM = "PLAIN";



    @Bean
    public ProducerFactory<String, MessageCreatedEvent> messageProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        if(!bootstrapServers.equals("localhost:9092")) {
            configProps.put("security.protocol", SECURITY_PROTOCOL);
            configProps.put("sasl.mechanism", SASL_MECHANISM);
            configProps.put("sasl.jaas.config", saslJaasConfig);
            configProps.put("client.dns.lookup",CLIENT_DNS);

        }

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        //  키 직렬화: StringSerializer
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        //  값 직렬화: JsonSerializer
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MessageCreatedEvent> messageKafkaTemplate() {
        // 실제 서비스에서 주입받아 사용할 '메시지 발송 도구'입니다.
        return new KafkaTemplate<>(messageProducerFactory());
    }
}