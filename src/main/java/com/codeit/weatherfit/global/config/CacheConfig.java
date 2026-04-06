package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.global.s3.properties.S3Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfig {


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                          S3Properties s3Properties) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );


        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(mapper,Object.class);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                 serializer
                        )
                )
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        Jackson2JsonRedisSerializer<Long> longSerializer = new Jackson2JsonRedisSerializer<>(mapper, Long.class);
        RedisCacheConfiguration countConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(longSerializer))
                .entryTtl(Duration.ofMinutes(10));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("presignedUrl",
                        config.entryTtl(Duration.ofSeconds(s3Properties.presignedUrlExpirationTime() * 2 / 3)))
                .withCacheConfiguration("feedCommentCount", countConfig)
                .withCacheConfiguration("feedLikeCount", countConfig)
                .build();


    }
}
