package com.tdd.movie.domain.event.service;

import com.tdd.movie.domain.event.EventPublisher;
import com.tdd.movie.domain.event.dto.OutboxEventCommand.CreateOutboxEventCommand;
import com.tdd.movie.domain.event.dto.OutboxEventCommand.PublishOutboxEventCommand;
import com.tdd.movie.domain.event.dto.OutboxEventParam.FindAllByStatusAndRetryCountAndRetryAtBeforeWithLockParam;
import com.tdd.movie.domain.event.dto.OutboxEventParam.GetByIdWithLockParam;
import com.tdd.movie.domain.event.model.OutboxEvent;
import com.tdd.movie.domain.event.model.OutboxEventStatus;
import com.tdd.movie.domain.event.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tdd.movie.domain.event.EventConstants.MAX_RETRY_COUNT;
import static com.tdd.movie.domain.event.model.OutboxEventStatus.FAILED;
import static java.time.LocalDateTime.now;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class OutboxEventCommandService {

    private final OutboxEventRepository outboxEventRepository;

    private final EventPublisher eventPublisher;

    public OutboxEvent createOutboxEvent(CreateOutboxEventCommand command) {
        return outboxEventRepository.save(OutboxEvent.builder()
                .type(command.event().getType())
                .payload(command.event().getPayload())
                .status(OutboxEventStatus.PENDING)
                .build());
    }

    public void publishOutboxEvent(PublishOutboxEventCommand command) {
        // DB 에서 조회해온다.
        final OutboxEvent outboxEvent = outboxEventRepository.getById(
                new GetByIdWithLockParam(command.id()));

        // OutboxEvent 의 상태값을 변경한다.
        outboxEvent.publish();
        try {
            outboxEventRepository.save(outboxEvent);
            // type(topic), payload(message)
            eventPublisher.publish(outboxEvent.getType(), outboxEvent.getPayload());
        } catch (RuntimeException e) {
            outboxEvent.fail();
            outboxEventRepository.save(outboxEvent);
            log.error("Failed to publish outbox event", e);
        }
    }

    // 아웃박스 이벤트를 재시도한다.
    public void retryFailedOutboxEvents() {
        // 실패한 아웃 박스 이벤트 목록을 가져온다.
        final List<OutboxEvent> failedEvents = outboxEventRepository.findAllByStatusAndRetryCountAndRetryAtBefore(
                new FindAllByStatusAndRetryCountAndRetryAtBeforeWithLockParam(
                        FAILED,
                        MAX_RETRY_COUNT,
                        now()
                )
        );

        // 순회하면서 재시도 한다.
        failedEvents.forEach(OutboxEvent::retry);

        outboxEventRepository.saveAll(failedEvents);
    }
}
