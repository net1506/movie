package com.tdd.movie.application;

import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueCommand.ActivateWaitingQueuesCommand;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueQuery.GetActiveTokenByUuidQuery;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueQuery.GetWaitingQueuePositionByUuidQuery;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueueWithPosition;
import com.tdd.movie.domain.waitingqueue.service.WaitingQueueCommandService;
import com.tdd.movie.domain.waitingqueue.service.WaitingQueueQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.tdd.movie.domain.waitingqueue.WaitingQueueConstants.ADD_PROCESSING_COUNT;

@Component
@RequiredArgsConstructor
public class WaitingQueueFacade {

    private final WaitingQueueQueryService waitingQueueQueryService;

    private final WaitingQueueCommandService waitingQueueCommandService;

    public String createWaitingQueueToken() {
        return waitingQueueCommandService.createWaitingQueue();
    }

    public WaitingQueueWithPosition getWaitingQueueWithPosition(String waitingQueueTokenUuid) {
        return waitingQueueQueryService.getWaitingQueuePosition(
                new GetWaitingQueuePositionByUuidQuery(waitingQueueTokenUuid));
    }

    /**
     * 대기열 토큰이 활성화 상태인지 검증
     *
     * @param waitingQueueUuid 대기열 토큰 UUID
     */
    public void validateWaitingQueueProcessing(String waitingQueueUuid) {
        waitingQueueQueryService.getWaitingQueueProcessing(
                new GetActiveTokenByUuidQuery(waitingQueueUuid));
    }

    @Transactional
    public void activateWaitingQueues() {
        waitingQueueCommandService.activateWaitingQueues(
                new ActivateWaitingQueuesCommand(ADD_PROCESSING_COUNT));
    }

    @Transactional
    public void expireOldWaitingQueues() {
        // 1일 (86400초) 이전 타임스탬프
        waitingQueueCommandService.expireOldWaitingQueues(86400L);
    }
}
