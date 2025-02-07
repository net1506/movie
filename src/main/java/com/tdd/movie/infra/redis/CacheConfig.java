package com.tdd.movie.infra.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.support.CacheName;
import com.tdd.movie.domain.theater.model.Theater;
import com.tdd.movie.domain.theater.model.TheaterSchedule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis를 캐시 저장소로 사용하는 캐시 설정 클래스.
 * <p>
 * - @Profile("!test"): 테스트 환경이 아닐 때만 이 설정이 활성화됨.
 * - @EnableCaching: 스프링 캐시 기능을 활성화.
 * - RedisCacheManager와 RedisCacheConfiguration을 설정하여 Redis를 캐시로 사용.
 */
@Profile("!test")  // "test" 프로필이 아닐 때만 이 설정이 적용됨 (테스트 환경에서는 캐시 사용 X)
@EnableCaching  // 스프링의 캐시 기능을 활성화 (Redis를 캐시 저장소로 사용)
@Configuration
public class CacheConfig {

    /**
     * Redis의 기본 캐시 설정 (기본적인 모든 캐시에 적용될 공통 설정) 을 정의하는 Bean.
     *
     * <p>
     * - 캐시 데이터를 JSON 형식으로 저장하도록 설정.
     * - null 값은 캐시에 저장하지 않도록 설정.
     * - 캐시 TTL(Time-To-Live, 만료 시간)은 1분으로 설정.
     *
     * @return RedisCacheConfiguration 객체
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 의 LocalDateTime 등의 직렬화를 지원하기 위해 모듈 추가
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.WRAPPER_OBJECT
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1)) // 캐시 TTL 을 1분으로 설정 (1분 후 자동 삭제)
                .disableCachingNullValues() // null 값은 캐시에 저장하지 않도록 설정
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                ) // Redis에서 key를 String 형태로 저장
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(objectMapper)
                        )
                ); // 캐시 값을 JSON 형식으로 직렬화하여 저장
    }

    /**
     * "특정 캐시별 세부 설정"을 담당!
     * Redis 캐시 매니저를 생성하는 Bean.
     * <p>
     * - 기본적으로 Redis 를 캐시 저장소로 사용.
     * - 특정 캐시(예: MOVIE, THEATER)에 대해 별도의 TTL 과 직렬화 설정을 적용.
     *
     * @param redisConnectionFactory Redis 연결 팩토리
     * @return RedisCacheManager 객체
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // JSON 직렬화를 위한 ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8의 날짜 및 시간 API(LocalDateTime 등) 지원
        objectMapper.registerModule(new JavaTimeModule());

        return RedisCacheManager.builder(redisConnectionFactory)
                .withCacheConfiguration(
                        CacheName.MOVIE,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .disableCachingNullValues() // null 값은 캐시에 저장하지 않도록 설정
                                .entryTtl(Duration.ofMinutes(2)) // 이 캐시의 TTL은 2분으로 설정 (2분 후 자동 삭제)
                                .serializeKeysWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new StringRedisSerializer())
                                ) // Redis에서 key를 String 형태로 저장
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer(objectMapper, Movie.class))
                                ) // 캐시 값을 JSON 형식으로 직렬화하여 저장
                )
                .withCacheConfiguration(
                        CacheName.THEATER, // 🎯 추가된 부분: Theater 캐시 설정
                        RedisCacheConfiguration.defaultCacheConfig()
                                .disableCachingNullValues() // null 값은 캐시에 저장하지 않도록 설정
                                .entryTtl(Duration.ofMinutes(2)) // 이 캐시의 TTL은 2분으로 설정 (2분 후 자동 삭제)
                                .serializeKeysWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new StringRedisSerializer())
                                ) // Redis에서 key를 String 형태로 저장
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(objectMapper, Theater.class))
                                ) // 캐시 값을 JSON 형식으로 직렬화하여 저장
                ).withCacheConfiguration(
                        CacheName.THEATER_SCHEDULE, // 🎯 추가된 부분: TheaterSchedule 캐시 설정
                        RedisCacheConfiguration.defaultCacheConfig()
                                .disableCachingNullValues() // null 값은 캐시에 저장하지 않도록 설정
                                .entryTtl(Duration.ofMinutes(2)) // 이 캐시의 TTL은 2분으로 설정 (2분 후 자동 삭제)
                                .serializeKeysWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new StringRedisSerializer())
                                ) // Redis에서 key를 String 형태로 저장
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(objectMapper, TheaterSchedule.class))
                                ) // 캐시 값을 JSON 형식으로 직렬화하여 저장
                )
                .build();
    }
}
