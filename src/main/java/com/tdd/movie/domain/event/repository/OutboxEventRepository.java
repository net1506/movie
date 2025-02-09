package com.tdd.movie.domain.event.repository;


import com.tdd.movie.domain.event.dto.OutboxEventParam.FindAllByStatusAndRetryCountAndRetryAtBeforeWithLockParam;
import com.tdd.movie.domain.event.dto.OutboxEventParam.FindByStatusParam;
import com.tdd.movie.domain.event.dto.OutboxEventParam.GetByIdParam;
import com.tdd.movie.domain.event.dto.OutboxEventParam.GetByIdWithLockParam;
import com.tdd.movie.domain.event.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository {

    OutboxEvent save(OutboxEvent outboxEvent);

    void saveAll(List<OutboxEvent> outboxEvents);

    OutboxEvent getById(GetByIdParam param);

    OutboxEvent getById(GetByIdWithLockParam param);

    OutboxEvent findByStatus(FindByStatusParam param);

    List<OutboxEvent> findAllByStatusAndRetryCountAndRetryAtBefore(
            FindAllByStatusAndRetryCountAndRetryAtBeforeWithLockParam param);

}
