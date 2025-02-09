package com.tdd.movie.application;

import com.tdd.movie.domain.event.dto.OutboxEventCommand.PublishOutboxEventCommand;
import com.tdd.movie.domain.event.model.OutboxEvent;
import com.tdd.movie.domain.event.service.OutboxEventCommandService;
import com.tdd.movie.domain.event.service.OutboxEventQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventFacade {

    private final OutboxEventCommandService outboxEventCommandService;

    private final OutboxEventQueryService outboxEventQueryService;

    /**
     * Outbox 테이블에서 처리 대기 중인 이벤트를 조회하고, 해당 이벤트를 발행하는 메서드.
     * - 처리할 이벤트가 없으면 바로 리턴.
     * - 이벤트가 있으면 PublishOutboxEventCommand 를 생성하여 이벤트를 발행.
     */
    public void publishOutboxEvent() {
        // Outbox 테이블에서 "처리 대기 중(PENDING)" 상태인 이벤트를 조회
        OutboxEvent event = outboxEventQueryService.findPendingOutboxEvent();

        // 처리할 이벤트가 없으면 메서드 종료
        if (event == null) {
            return;
        }

        // 조회된 이벤트를 기반으로 새로운 PublishOutboxEventCommand 를 생성하여 발행 요청
        outboxEventCommandService.publishOutboxEvent(new PublishOutboxEventCommand(event.getId()));
    }

    /**
     * 실패한 Outbox 이벤트들을 재시도하는 메서드.
     * - outboxEventCommandService 내부에서 실패한 이벤트들을 찾아 다시 처리함.
     */
    public void retryFailedOutboxEvents() {
        outboxEventCommandService.retryFailedOutboxEvents();
    }
}
