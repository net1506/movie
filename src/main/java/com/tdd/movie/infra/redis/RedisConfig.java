package com.tdd.movie.infra.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueue;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";

    private final RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(
                        REDISSON_HOST_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort());
        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    /**
     * RedisTemplate 은 Redis 데이터를 저장하거나 가져오는 데 사용되는 Spring Data Redis 의 핵심 컴포넌트입이다.
     * RedisConnectionFactory 를 기반으로 Key-Value 형태로 데이터를 저장 및 조회할 때 사용한다.
     * RedisSerializer 를 설정하여 JSON 데이터 직렬화 처리를 수행한다.
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new Jackson2JsonRedisSerializer(objectMapper, Object.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer(objectMapper, WaitingQueue.class));

        return template;
    }
}