package com.tdd.movie.domain.waitingqueue.service;

import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueQuery.GetActiveTokenByUuidQuery;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueQuery.GetWaitingQueuePositionByUuidQuery;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.GetActiveTokenByUuidParam;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.GetWaitingQueuePositionByUuidParam;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueue;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueueWithPosition;
import com.tdd.movie.domain.waitingqueue.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WaitingQueueQueryService {

    private final WaitingQueueRepository waitingQueueRepository;

    public WaitingQueueWithPosition getWaitingQueuePosition(
            GetWaitingQueuePositionByUuidQuery query) {
        Long position = waitingQueueRepository.getWaitingQueuePosition(
                new GetWaitingQueuePositionByUuidParam(query.uuid()));

        return new WaitingQueueWithPosition(
                query.uuid(),
                position
        );
    }

    public WaitingQueue getWaitingQueueProcessing(GetActiveTokenByUuidQuery query) {
        WaitingQueue waitingQueue = waitingQueueRepository.getActiveToken(
                new GetActiveTokenByUuidParam(query.uuid()));

        waitingQueue.validateProcessing();

        return waitingQueue;
    }
}
