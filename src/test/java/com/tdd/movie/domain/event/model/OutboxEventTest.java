package com.tdd.movie.domain.event.model;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tdd.movie.domain.event.EventConstants.MAX_RETRY_COUNT;
import static com.tdd.movie.domain.event.EventConstants.RETRY_INTERVAL_MINUTES;
import static com.tdd.movie.domain.event.model.OutboxEventStatus.*;
import static com.tdd.movie.domain.support.error.ErrorType.OutboxEvent.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OutboxEvent 단위 테스트")
class OutboxEventTest {

    @Nested
    @DisplayName("이벤트 발행")
    class PublishTest {

        @Test
        @DisplayName("이벤트 발행 실패 - 이미 발행된 이벤트인 경우")
        void shouldThrowExceptionWhenEventIsAlreadyPublished() {

            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(PUBLISHED)
                    .build();

            // when
            // 상태를 PUBLISHED 로 변경하려고 한다.
            final CoreException exception = assertThrows(CoreException.class, () -> event.publish());

            // then
            assertThat(exception.getErrorType()).isEqualTo(OUTBOX_EVENT_ALREADY_PUBLISHED);
        }

        @Test
        @DisplayName("이벤트 발행 성공")
        void shouldSuccessfullyPublishEvent() {
            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(PENDING)
                    .build();

            // when
            event.publish();

            // then
            assertThat(event.getStatus()).isEqualTo(PUBLISHED);
        }
    }

    @Nested
    @DisplayName("이벤트 실패")
    class FailTest {

        @Test
        @DisplayName("이벤트 실패 - 이미 실패한 이벤트인 경우")
        void shouldThrowExceptionWhenEventIsAlreadyFailed() {
            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(FAILED)
                    .build();

            // when
            // 아웃 박스 이벤트의 상태를 실패로 변경하려고 한다.
            final CoreException exception = assertThrows(CoreException.class, () -> event.fail());

            // then
            assertThat(exception.getErrorType()).isEqualTo(OUTBOX_EVENT_ALREADY_FAILED);
        }

        @Test
        @DisplayName("이벤트 실패 성공")
        void shouldSuccessfullyFailEvent() {
            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(PENDING)
                    .build();

            // when
            event.fail();

            // then
            assertThat(event.getStatus()).isEqualTo(FAILED);
        }
    }

    @Nested
    @DisplayName("이벤트 재시도")
    class RetryTest {

        @Test
        @DisplayName("이벤트 재시도 - 이미 실패한 이벤트가 아닌 경우")
        void shouldThrowExceptionWhenEventIsNotFailed() {
            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(PENDING)
                    .build();

            // when
            // retry 는 아웃 박스 이벤트의 상태를 PENDING 로 변경 시켜준다.
            final CoreException exception = assertThrows(CoreException.class, event::retry);

            // then
            assertThat(exception.getErrorType()).isEqualTo(OUTBOX_EVENT_NOT_FAILED);
        }

        @Test
        @DisplayName("이벤트 재시도 - 재시도 횟수 초과")
        void shouldThrowExceptionWhenRetryCountExceeds() {

            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(OutboxEventStatus.FAILED)
                    .retryCount(MAX_RETRY_COUNT) // 재시도 횟수가 이미 3회
                    .build();

            // when
            // retry 는 아웃 박스 이벤트의 상태를 PENDING 로 변경 시켜준다.
            final CoreException exception = assertThrows(CoreException.class, event::retry);

            // then
            assertThat(exception.getErrorType()).isEqualTo(OUTBOX_EVENT_RETRY_EXCEEDED);
        }

        @Test
        @DisplayName("이벤트 재시도 - updateAt이 null인 경우")
        void shouldThrowExceptionWhenUpdatedAtIsNull() {
            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(OutboxEventStatus.FAILED)
                    .retryCount(0)
                    .build();

            // when
            // retryAt(재시도 가능 시간) 이 있어야 재시도가 가능함
            final CoreException exception = assertThrows(CoreException.class, event::retry);

            // then
            assertThat(exception.getErrorType()).isEqualTo(
                    ErrorType.OutboxEvent.OUTBOX_EVENT_UPDATED_AT_NULL);
        }

        @Test
        @DisplayName("이벤트 재시도 - 재시도 간격 미만")
        void shouldThrowExceptionWhenRetryIntervalNotPassed() {
            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(OutboxEventStatus.FAILED)
                    .retryCount(0)
                    .retryAt(now().plusSeconds(1))
                    .build();

            // when
            final CoreException exception = assertThrows(CoreException.class, event::retry);

            // then
            assertThat(exception.getErrorType()).isEqualTo(
                    ErrorType.OutboxEvent.OUTBOX_EVENT_RETRY_INTERVAL_NOT_PASSED);
        }

        @Test
        @DisplayName("이벤트 재시도 성공")
        void shouldSuccessfullyRetryEvent() {
            // given
            final OutboxEvent event = OutboxEvent.builder()
                    .status(OutboxEventStatus.FAILED) // 아웃 박스 이벤트의 상태 실패
                    .retryCount(0) // 재시도 횟수 0
                    .retryAt(now().minusMinutes(RETRY_INTERVAL_MINUTES)) // 재시도 가능 시간은 5분이나 이미 지남
                    .build();

            // when
            event.retry();

            // then
            assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        }
    }

}