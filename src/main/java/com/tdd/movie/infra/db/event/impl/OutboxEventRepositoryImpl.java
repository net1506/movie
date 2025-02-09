package com.tdd.movie.infra.db.event.impl;

import com.tdd.movie.domain.event.dto.OutboxEventParam.FindAllByStatusAndRetryCountAndRetryAtBeforeWithLockParam;
import com.tdd.movie.domain.event.dto.OutboxEventParam.FindByStatusParam;
import com.tdd.movie.domain.event.dto.OutboxEventParam.GetByIdParam;
import com.tdd.movie.domain.event.dto.OutboxEventParam.GetByIdWithLockParam;
import com.tdd.movie.domain.event.model.OutboxEvent;
import com.tdd.movie.domain.event.repository.OutboxEventRepository;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import com.tdd.movie.infra.db.event.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return outboxEventJpaRepository.save(outboxEvent);
    }

    @Override
    public void saveAll(List<OutboxEvent> outboxEvents) {
        outboxEventJpaRepository.saveAll(outboxEvents);
    }

    @Override
    public OutboxEvent getById(GetByIdParam param) {
        return outboxEventJpaRepository.findById(param.id())
                .orElseThrow(() -> new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_NOT_FOUND));
    }

    @Override
    public OutboxEvent getById(GetByIdWithLockParam param) {
        return outboxEventJpaRepository.findByIdWithLock(param.id())
                .orElseThrow(() -> new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_NOT_FOUND));
    }

    @Override
    public OutboxEvent findByStatus(FindByStatusParam param) {
        return outboxEventJpaRepository.findByStatus(param.status()).orElse(null);
    }

    @Override
    public List<OutboxEvent> findAllByStatusAndRetryCountAndRetryAtBefore(
            FindAllByStatusAndRetryCountAndRetryAtBeforeWithLockParam param) {
        return outboxEventJpaRepository.findAllByStatusAndRetryCountAndRetryAtBefore(
                param.status(), param.maxRetryCount(), param.retryAtBefore());
    }

}
