package com.tdd.movie.domain.waitingqueue.repository;

import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.ActivateWaitingQueuesParam;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.GetActiveTokenByUuidParam;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.GetWaitingQueuePositionByUuidParam;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.RemoveActiveTokenParam;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueue;

import java.util.List;

public interface WaitingQueueRepository {
    // 대기열 추가
    String addWaitingQueue(String uuid);

    // 대기열 활성화
    void activateWaitingQueues(ActivateWaitingQueuesParam param);

    // 대기열 순번 조회
    Long getWaitingQueuePosition(GetWaitingQueuePositionByUuidParam param);

    // 활성화 된 토큰 조회
    WaitingQueue getActiveToken(GetActiveTokenByUuidParam param);

    // 활성화 된 모든 토큰 조회
    List<WaitingQueue> getAllActiveTokens();

    // 활성화된 토큰 제거
    void removeActiveToken(RemoveActiveTokenParam param);
}
