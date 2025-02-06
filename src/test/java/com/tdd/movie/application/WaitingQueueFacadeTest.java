package com.tdd.movie.application;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.ActivateWaitingQueuesParam;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueue;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueueWithPosition;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterSeatJpaRepository;
import com.tdd.movie.infra.redis.waitingqueue.WaitingQueueRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("WaitingQueueFacade 통합 테스트")
class WaitingQueueFacadeTest {

    @Autowired
    WaitingQueueFacade waitingQueueFacade;

    @Autowired
    MovieJpaRepository movieJpaRepository;

    @Autowired
    TheaterJpaRepository theaterJpaRepository;

    @Autowired
    TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @Autowired
    TheaterSeatJpaRepository theaterSeatJpaRepository;

    @Autowired
    WaitingQueueRedisRepository waitingQueueRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        movieJpaRepository.deleteAll();
        theaterJpaRepository.deleteAll();
        theaterScheduleJpaRepository.deleteAll();
        theaterSeatJpaRepository.deleteAll();

        redisTemplate.keys("*").forEach(redisTemplate::delete);
    }

    @Nested
    @DisplayName(" 단위 테스트")
    class CreateWaitingQueueTest {
        @Test
        @DisplayName("대기열 생성 성공")
        public void shouldSuccessfullyCreateWaitingQueue() throws Exception {
            // given
            // when
            final String result = waitingQueueFacade.createWaitingQueueToken();

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("토큰 대기 번호 조회")
    class GetWaitingQueueWithPositionTest {

        @Test
        @DisplayName("토큰 대기 번호 조회 실패 - 존재하지 않는 토큰")
        void shouldThrowExceptionWhenGetWaitingQueueWithPositionWithNonExistToken() {
            // given
            final String waitingQueueUuid = "non-exist-waiting-queue-uuid";

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                waitingQueueFacade.getWaitingQueueWithPosition(waitingQueueUuid);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.WaitingQueue.WAITING_QUEUE_NOT_FOUND);
        }

        @Test
        @DisplayName("토큰 대기 번호 조회 성공 - 활성 토큰")
        void shouldSuccessfullyGetWaitingQueueWithPositionWhenActiveToken() {
            // given
            final String uuid = UUID.randomUUID().toString();
            waitingQueueRepository.addWaitingQueue(uuid); // 대기열 생성
            // 현재 시간 초가 score 로 들어가고, 0 -10 가 가져올 때 작은 것이 높은 순위로 잡혀서 가져옴
            waitingQueueRepository.activateWaitingQueues(
                    new ActivateWaitingQueuesParam(1, 1, TimeUnit.MINUTES));

            // when
            final WaitingQueueWithPosition result = waitingQueueFacade.getWaitingQueueWithPosition(uuid);

            // then
            assertThat(result.uuid()).isEqualTo(uuid);
            assertThat(result.position()).isEqualTo(0);
        }

        @Test
        @DisplayName("토큰 대기 번호 조회 성공 - 대기 중인 토큰")
        void shouldSuccessfullyGetWaitingQueueWithPosition() {
            // given
            final String uuid = UUID.randomUUID().toString();
            waitingQueueRepository.addWaitingQueue(uuid);

            // when
            final WaitingQueueWithPosition result = waitingQueueFacade.getWaitingQueueWithPosition(uuid);

            // then
            assertThat(result.uuid()).isEqualTo(uuid);
            assertThat(result.position()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("토큰 활성 여부 확인")
    class ValidateWaitingQueueProcessingTest {

        @Test
        @DisplayName("토큰 활성 여부 확인 실패 - 활성 토큰이 아닌 경우")
        void shouldThrowExceptionWhenValidateWaitingQueueProcessingWithNonActiveToken() {
            // given
            final String waitingQueueUuid = "non-active-waiting-queue-uuid";

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                waitingQueueFacade.validateWaitingQueueProcessing(waitingQueueUuid);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.WaitingQueue.ACTIVE_QUEUE_NOT_FOUND);
        }

        @Test
        @DisplayName("토큰 활성 여부 확인 실패 - 만료 된 토큰인 경우")
        void shouldThrowExceptionWhenValidateWaitingQueueProcessingWithExpiredToken() {
            // given
            final String waitingQueueUuid = UUID.randomUUID().toString();
            waitingQueueRepository.addWaitingQueue(waitingQueueUuid);
            waitingQueueRepository.activateWaitingQueues(
                    new ActivateWaitingQueuesParam(1, 1, TimeUnit.MILLISECONDS));

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                waitingQueueFacade.validateWaitingQueueProcessing(waitingQueueUuid);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.WaitingQueue.WAITING_QUEUE_EXPIRED);
        }

        @Test
        @DisplayName("토큰 활성 여부 확인 성공")
        void shouldSuccessfullyValidateWaitingQueueProcessing() {
            // given
            final String uuid = UUID.randomUUID().toString();
            waitingQueueRepository.addWaitingQueue(uuid);
            waitingQueueRepository.activateWaitingQueues(
                    new ActivateWaitingQueuesParam(1, 1, TimeUnit.MINUTES));

            // when & then
            waitingQueueFacade.validateWaitingQueueProcessing(uuid);
        }
    }

    @Nested
    @DisplayName("대기 토큰 활성화")
    class ActivateWaitingQueuesTest {

        @Test
        @DisplayName("대기 토큰 활성화 성공 - 대기 토큰이 없는 경우")
        void shouldSuccessfullyActivateWaitingQueuesWhenNoWaitingQueues() {
            // given
            // when
            waitingQueueFacade.activateWaitingQueues();

            // then
            final List<WaitingQueue> activeTokens =
                    waitingQueueRepository.getAllActiveTokens();
            assertThat(activeTokens).isEmpty();
        }

        @Test
        @DisplayName("대기 토큰 활성화 성공")
        void shouldSuccessfullyActivateWaitingQueues() {
            // given
            final String uuid1 = UUID.randomUUID().toString();
            final String uuid2 = UUID.randomUUID().toString();
            final String uuid3 = UUID.randomUUID().toString();
            waitingQueueRepository.addWaitingQueue(uuid1);
            waitingQueueRepository.addWaitingQueue(uuid2);
            waitingQueueRepository.addWaitingQueue(uuid3);

            // when
            waitingQueueFacade.activateWaitingQueues();

            // then
            final List<WaitingQueue> activeTokens =
                    waitingQueueRepository.getAllActiveTokens();
            assertThat(activeTokens).hasSize(3);
        }
    }
}