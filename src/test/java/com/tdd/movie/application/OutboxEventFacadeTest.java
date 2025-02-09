package com.tdd.movie.application;

import com.tdd.movie.domain.event.EventConstants;
import com.tdd.movie.domain.event.EventPublisher;
import com.tdd.movie.domain.event.model.OutboxEvent;
import com.tdd.movie.domain.event.model.OutboxEventStatus;
import com.tdd.movie.domain.support.Event;
import com.tdd.movie.infra.db.event.OutboxEventJpaRepository;
import com.tdd.movie.interfaces.consumer.KafkaConsumer;
import com.tdd.movie.interfaces.consumer.PaymentKafkaConsumer;
import com.tdd.movie.kafka.TestEvent;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static com.tdd.movie.domain.event.EventConstants.RETRY_INTERVAL_MINUTES;
import static com.tdd.movie.domain.event.model.OutboxEventStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OutboxEventFacade 통합 테스트")
public class OutboxEventFacadeTest {

    @Autowired
    private OutboxEventFacade outboxEventFacade;

    @Autowired
    private OutboxEventJpaRepository outboxEventJpaRepository;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Autowired
    private PaymentKafkaConsumer paymentKafkaConsumer;

    @SpyBean
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        outboxEventJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("outbox 이벤트 발행 테스트")
    class PublishOutboxEventTest {
        @Test
        @DisplayName("outbox 이벤트 발행 실패 - event가 존재하지 않는 경우")
        void shouldThrowExceptionWhenIdIsNotExist() {
            // given

            // when
            outboxEventFacade.publishOutboxEvent();
        }

        @Test
        @DisplayName("outbox 이벤트 발행 성공 - test topic 이벤트 발행")
        void shouldSuccessfullyPublishOutboxEvent() {
            // given
            LocalDateTime now = LocalDateTime.now();
            final Event event = new TestEvent(now.toString());
            // 아웃 박스 이벤트 생성 및 등록
            final Long eventId = outboxEventJpaRepository.save(
                    OutboxEvent.builder()
                            .status(PENDING) // 대기 상태
                            .type(event.getType()) // topic: MY_TOPIC
                            .payload(event.getPayload()) // 값은 현재 시간 날짜 now()
                            .build()
            ).getId();

            // when
            outboxEventFacade.publishOutboxEvent(); // KafkaProducer 가 메시지를 발행한다.

            // then
            final OutboxEvent result = outboxEventJpaRepository.findById(eventId).orElseThrow();
            assertThat(result.getStatus()).isEqualTo(PUBLISHED);

            Awaitility.await()
                    .atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(
                            () -> assertThat(KafkaConsumer.getMessages()).contains(event.getPayload()));
        }

        @Test
        @DisplayName("outbox 이벤트 발행 성공 - payment success 이벤트 발행")
        void shouldSuccessfullyPublishPaymentSuccessEvent() {
            // given
            LocalDateTime currentTime = LocalDateTime.now();
            long currentSecond = currentTime.toEpochSecond(ZoneOffset.UTC);
            final Event event = new TestEvent(String.valueOf(currentSecond));
            final Long eventId = outboxEventJpaRepository.save(
                    OutboxEvent.builder()
                            .status(OutboxEventStatus.PENDING) // 대기 상태
                            .type(event.getType()) // PaymentSuccess Type
                            .payload(event.getPayload()) // 숫자 Long 값
                            .build()
            ).getId();

            // when
            outboxEventFacade.publishOutboxEvent(); // KafkaProducer 가 메시지를 발행한다.

            // then
            final OutboxEvent result = outboxEventJpaRepository.findById(eventId).orElseThrow();
            assertThat(result.getStatus()).isEqualTo(PUBLISHED);

            Awaitility.await()
                    .atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(
                            () -> assertThat(KafkaConsumer.getMessages()).contains(event.getPayload()));
        }

        @Test
        @DisplayName("outbox 이벤트 발행 실패 - kafka publish 실패")
        void shouldFailToPublishOutboxEvent() {
            LocalDateTime now = LocalDateTime.now();
            final Event event = new TestEvent(now.toString());
            final Long eventId = outboxEventJpaRepository.save(
                    OutboxEvent.builder()
                            .status(PENDING)
                            .type(event.getType())
                            .payload(event.getPayload())
                            .build()
            ).getId();
            doThrow(new RuntimeException()).when(eventPublisher)
                    .publish(event.getType(), event.getPayload());

            // when
            outboxEventFacade.publishOutboxEvent();

            // then
            final OutboxEvent result = outboxEventJpaRepository.findById(eventId).orElseThrow();
            assertThat(result.getStatus()).isEqualTo(FAILED);
            // 재시도 가능 시간 > 현재 + 5 MINUTE
            assertThat(result.getRetryAt()).isAfter(
                    now.plusMinutes(RETRY_INTERVAL_MINUTES));
        }
    }

    @Nested
    @DisplayName("outbox 이벤트 재시도 테스트")
    class RetryFailedOutboxEventsTest {

        @Test
        @DisplayName("outbox 이벤트 재시도 실패 - retryAt이 지나지 않은 경우")
        void shouldFailToRetryOutboxEventWhenRetryIntervalNotPassed() {
            // given
            final Long eventId =
                    outboxEventJpaRepository.save(
                            OutboxEvent.builder()
                                    .type("test")
                                    .payload("test")
                                    .status(FAILED)
                                    .retryCount(0)
                                    .retryAt(LocalDateTime.now().plusMinutes(1)) // 재시도 1분 뒤 가능
                                    .build()
                    ).getId();

            // when
            outboxEventFacade.retryFailedOutboxEvents();

            // then
            OutboxEvent result = outboxEventJpaRepository.findById(eventId).orElseThrow();
            assertThat(result.getStatus()).isEqualTo(FAILED);
        }

        @Test
        @DisplayName("outbox 이벤트 재시도 실패 - retryCount가 MAX_RETRY_COUNT인 경우")
        void shouldFailToRetryOutboxEventWhenRetryCountExceeded() {
            // given
            final Long eventId = outboxEventJpaRepository.save(
                    OutboxEvent.builder()
                            .type("test")
                            .payload("test")
                            .status(OutboxEventStatus.FAILED)
                            .retryCount(EventConstants.MAX_RETRY_COUNT) // 재시도한 횟수가 이미 3회
                            .retryAt(LocalDateTime.now().minusMinutes(RETRY_INTERVAL_MINUTES))
                            .build()
            ).getId();

            // when
            outboxEventFacade.retryFailedOutboxEvents();

            // then
            final OutboxEvent result = outboxEventJpaRepository.findById(eventId).orElseThrow();
            assertThat(result.getStatus()).isEqualTo(FAILED);
        }

        @Test
        @DisplayName("outbox 이벤트 재시도 성공")
        void shouldSuccessfullyRetryFailedOutboxEvents() {
            // given
            LocalDateTime now = LocalDateTime.now();
            final Event event = new TestEvent(now.toString());
            final Long eventId = outboxEventJpaRepository.save(
                    OutboxEvent.builder()
                            .status(OutboxEventStatus.FAILED)
                            .type(event.getType())
                            .payload(event.getPayload())
                            .retryCount(EventConstants.MAX_RETRY_COUNT - 1)
                            .retryAt(now)
                            .build()
            ).getId();

            // when
            outboxEventFacade.retryFailedOutboxEvents();

            // then
            final OutboxEvent result = outboxEventJpaRepository.findById(eventId).orElseThrow();
            assertThat(result.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
            assertThat(result.getRetryCount()).isEqualTo(EventConstants.MAX_RETRY_COUNT);
        }
    }
}
