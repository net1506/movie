package com.tdd.movie.domain.event.dto;

import com.tdd.movie.domain.support.Event;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;

public record OutboxEventCommand() {

    public record CreateOutboxEventCommand(Event event) {

        // 아웃 박스 이벤트를 생성한다.
        public CreateOutboxEventCommand {
            if (event == null) {
                throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_MUST_NOT_BE_NULL);
            }
        }

    }

    // 아웃 박스 이벤트를 발행한다.
    public record PublishOutboxEventCommand(Long id) {

        public PublishOutboxEventCommand {
            if (id == null) {
                throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_ID_MUST_NOT_BE_NULL);
            }
        }

    }

}
