package com.tdd.movie.domain.waitingqueue.dto;

import com.tdd.movie.domain.support.error.CoreException;

import static com.tdd.movie.domain.support.error.ErrorType.WaitingQueue.WAITING_QUEUE_UUID_MUST_NOT_BE_EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class WaitingQueueQuery {

    public record GetWaitingQueueByUuidQuery(String uuid) {

        public GetWaitingQueueByUuidQuery {
            if (uuid == null || uuid.isEmpty()) {
                throw new CoreException(WAITING_QUEUE_UUID_MUST_NOT_BE_EMPTY);
            }
        }
    }

    // 대기열 순번 조회 By Uuid
    public record GetWaitingQueuePositionByUuidQuery(String uuid) {

        public GetWaitingQueuePositionByUuidQuery {
            if (uuid == null || uuid.isEmpty()) {
                throw new CoreException(WAITING_QUEUE_UUID_MUST_NOT_BE_EMPTY);
            }
        }
    }

    public record GetActiveTokenByUuidQuery(String uuid) {

        public GetActiveTokenByUuidQuery {
            if (isEmpty(uuid)) {
                throw new CoreException(WAITING_QUEUE_UUID_MUST_NOT_BE_EMPTY);
            }
        }
    }
}
