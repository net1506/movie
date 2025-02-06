package com.tdd.movie.domain.waitingqueue.dto;

import com.tdd.movie.domain.support.error.CoreException;

import static com.tdd.movie.domain.support.error.ErrorType.WaitingQueue.*;

public class WaitingQueueCommand {
    public record ActivateWaitingQueuesCommand(Integer availableSlots) {

        public ActivateWaitingQueuesCommand {
            if (availableSlots == null) {
                throw new CoreException(AVAILABLE_SLOTS_MUST_NOT_BE_NULL);
            }
            if (availableSlots <= 0) {
                throw new CoreException(AVAILABLE_SLOTS_MUST_BE_POSITIVE);
            }
        }
    }

    // 활성화된 대기열 만료 명령 Command
    public record ExpireActivatedWaitingQueueCommand(String uuid) {

        public ExpireActivatedWaitingQueueCommand {
            if (uuid == null) {
                throw new CoreException(UUID_MUST_NOT_BE_NULL);
            }
        }
    }
}
