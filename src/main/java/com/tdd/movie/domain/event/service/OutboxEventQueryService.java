package com.tdd.movie.domain.event.service;

import com.tdd.movie.domain.event.dto.OutboxEventParam.FindByStatusParam;
import com.tdd.movie.domain.event.model.OutboxEvent;
import com.tdd.movie.domain.event.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tdd.movie.domain.event.model.OutboxEventStatus.PENDING;


@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OutboxEventQueryService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEvent findPendingOutboxEvent() {
        return outboxEventRepository.findByStatus(new FindByStatusParam(PENDING));
    }
}
