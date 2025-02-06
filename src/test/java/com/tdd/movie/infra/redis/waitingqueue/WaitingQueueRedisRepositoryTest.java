package com.tdd.movie.infra.redis.waitingqueue;

import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.ActivateWaitingQueuesParam;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.GetActiveTokenByUuidParam;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.GetWaitingQueuePositionByUuidParam;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.RemoveActiveTokenParam;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("WaitingQueueRedisRepository 테스트")
class WaitingQueueRedisRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(WaitingQueueRedisRepositoryTest.class);

    @Autowired
    private WaitingQueueRedisRepository waitingQueueRedisRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${queue.waiting-key}")
    private String waitingQueueKey;
    @Value("${queue.active-key}")
    private String activeQueueKey;

    @BeforeEach
    void setUp() {
        log.info("waitingQueueKey: {}", waitingQueueKey);
        log.info("activeQueueKey: {}", activeQueueKey);
        redisTemplate.keys("*").forEach(redisTemplate::delete);
    }

    /**
     * 1. concertWaitingQueue 레코드 셋을 생성한다.
     * 2. key: UUID
     * 3. value: 현재 시간을 초로 환산한 Long 값
     * <p>
     * redisTemplate.opsForZSet().add(waitingQueueKey, uuid, now().toEpochSecond(ZoneOffset.UTC))
     */
    @Test
    @DisplayName("대기열 추가")
    void shouldSuccessfullyAddWaitingQueue() {
        // given
        final String uuid = UUID.randomUUID().toString();

        // when
        final String result = waitingQueueRedisRepository.addWaitingQueue(uuid); // waiting 키에 삽입

        // then
        assertThat(result).isEqualTo(uuid);
    }

    /**
     * 1. concertWaitingQueue 레코드 셋에 있던 데이터는 삭제한다.
     * 2. concertActiveQueue 레코드 셋을 생성한다.
     * 3. key: UUID
     * 4. value: 현재 시간을 초로 환산한 Long 값
     */
    @Test
    @DisplayName("대기열 활성화")
    void shouldSuccessfullyActivateWaitingQueues() {
        // given
        final String uuid = UUID.randomUUID().toString();

        // key, value, score
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid, 1);
        // 1개 여유 공간, 1분 유효 대기열
        final ActivateWaitingQueuesParam param = new ActivateWaitingQueuesParam(1, 1, TimeUnit.MINUTES);

        // when
        waitingQueueRedisRepository.activateWaitingQueues(param);

        // then
        final Long waitingQueueSize = redisTemplate.opsForZSet().size(waitingQueueKey);
        final Double score = redisTemplate.opsForZSet().score(activeQueueKey, uuid);
        assertThat(waitingQueueSize).isZero();
        assertThat(score).isNotNull();
    }

    @Test
    @DisplayName("대기열 순번 조회")
    void shouldSuccessfullyGetWaitingQueuePosition() {
        // given
        final String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid, LocalDateTime.now().toEpochSecond(
                ZoneOffset.UTC));
        final String uuid2 = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid2, LocalDateTime.now().plusSeconds(1).toEpochSecond(
                ZoneOffset.UTC));
        final String uuid3 = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid3, LocalDateTime.now().plusSeconds(2).toEpochSecond(
                ZoneOffset.UTC));
        final String uuid4 = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid4, LocalDateTime.now().plusSeconds(3).toEpochSecond(
                ZoneOffset.UTC));

        final GetWaitingQueuePositionByUuidParam param = new GetWaitingQueuePositionByUuidParam(uuid3);

        // when
        final Long result = waitingQueueRedisRepository.getWaitingQueuePosition(param); // 제일 작은 것부터 1 부터 순위 시작

        // then
        assertThat(result).isEqualTo(3L);
    }

    @Test
    @DisplayName("활성 토큰 조회")
    void shouldSuccessfullyGetActiveToken() {
        // given
        final String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, 1);
        final GetActiveTokenByUuidParam param = new GetActiveTokenByUuidParam(uuid);

        // when
        final WaitingQueue result = waitingQueueRedisRepository.getActiveToken(param);

        // then
        assertThat(result.getUuid()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("활성 토큰 전체 조회")
    void shouldSuccessfullyGetActiveTokens() {
        // given
        final String uuid1 = UUID.randomUUID().toString();
        final String uuid2 = UUID.randomUUID().toString();
        final String uuid3 = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid1, 1);
        redisTemplate.opsForZSet().add(activeQueueKey, uuid2, 2);
        redisTemplate.opsForZSet().add(activeQueueKey, uuid3, 3);

        // when
        final List<WaitingQueue> result = waitingQueueRedisRepository.getAllActiveTokens();

        // then
        assertThat(result).extracting(WaitingQueue::getUuid)
                .containsExactlyInAnyOrder(uuid1, uuid2, uuid3);
    }

    @Test
    @DisplayName("활성 토큰 삭제")
    void shouldSuccessfullyRemoveActiveToken() {
        // given
        final String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, 1);

        // when
        waitingQueueRedisRepository.removeActiveToken(new RemoveActiveTokenParam(uuid));

        // then
        final Long result = redisTemplate.opsForZSet().size(activeQueueKey);
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("하루가 끝날 때까지 남은 시간(초) 계산")
    public void calculateRemainingSecondsUntilEndOfDay() throws Exception {
        // given
        LocalDateTime predefinedTime = LocalDateTime.ofEpochSecond(99999111, 0, ZoneOffset.UTC);

        // 현재 하루의 종료 시간 (예: 2025-01-27T23:59:59.999999999)
        LocalDateTime todayEndTime = LocalDate.now().atTime(LocalTime.MAX);
        System.out.println("End of Today: " + todayEndTime);

        // 현재 시간 (예: 2025-01-27T10:24:29.611879)
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("Current Time: " + currentTime);

        // 하루 종료 시간을 Epoch 초로 변환
        long todayEndSecond = todayEndTime.toEpochSecond(ZoneOffset.UTC);
        System.out.println("End of Today (Epoch seconds): " + todayEndSecond);

        // 현재 시간을 Epoch 초로 변환
        long currentSecond = currentTime.toEpochSecond(ZoneOffset.UTC);
        System.out.println("Current Time (Epoch seconds): " + currentSecond);

        // 하루 종료까지 남은 시간 (초)
        long remainingTime = todayEndSecond - currentSecond;
        System.out.println("Remaining Time (seconds): " + remainingTime);

        // then
        assertTrue(remainingTime > 0, "Remaining time should be positive");
    }
}