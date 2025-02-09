package com.tdd.movie.domain.event.dto;

import com.tdd.movie.domain.event.dto.OutboxEventCommand.CreateOutboxEventCommand;
import com.tdd.movie.domain.event.dto.OutboxEventCommand.PublishOutboxEventCommand;
import com.tdd.movie.domain.payment.event.PaymentSuccessEvent;
import com.tdd.movie.domain.support.Event;
import com.tdd.movie.domain.support.error.CoreException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tdd.movie.domain.support.error.ErrorType.OutboxEvent.OUTBOX_EVENT_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.OutboxEvent.OUTBOX_EVENT_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OutboxEventCommand 단위 테스트")
class OutboxEventCommandTest {

    @Nested
    @DisplayName("CreateOutboxEventCommandTest 생성자 생성 테스트")
    class CreateOutboxEventCommandTest {

        @Test
        @DisplayName("CreateOutboxEventCommandTest 생성자 생성 실패 - Event 가 NULL 인 경우")
        public void shouldThrowExceptionWhenEventIsNull() throws Exception {
            // given
            final Event event = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new CreateOutboxEventCommand(event));

            // then
            assertThat(coreException).isNotNull();
            assertThat(coreException.getErrorType()).isEqualTo(OUTBOX_EVENT_MUST_NOT_BE_NULL);
            assertThat(coreException.getMessage()).isEqualTo(OUTBOX_EVENT_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("CreateOutboxEventCommandTest 생성자 생성 성공")
        public void shouldSuccessCreateOutboxEventCommand() throws Exception {
            // given
            final Event event = new PaymentSuccessEvent(1L);

            // when
            CreateOutboxEventCommand createOutboxEventCommand = new CreateOutboxEventCommand(event);

            // then
            assertThat(createOutboxEventCommand).isNotNull();
        }
    }

    @Nested
    @DisplayName("PublishOutboxEventCommand 단위 테스트")
    class PublishOutboxEventCommandTest {

        @Test
        @DisplayName("아웃 박스 이벤트 발행 실패 - ID Is NULL 인 경우")
        public void shouldThrowExceptionWhenIsNull() throws Exception {
            // given
            Long id = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new PublishOutboxEventCommand(id));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(OUTBOX_EVENT_ID_MUST_NOT_BE_NULL);
            assertThat(coreException.getMessage()).isEqualTo(OUTBOX_EVENT_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("아웃 박스 이벤트 발행 성공")
        public void shouldPublishOutboxEventCommand() throws Exception {
            // given
            Long id = 1L;

            // when
            PublishOutboxEventCommand publishOutboxEventCommand = new PublishOutboxEventCommand(id);

            // then
            assertThat(publishOutboxEventCommand.id()).isEqualTo(id);
        }
    }

}