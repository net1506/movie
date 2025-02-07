package com.tdd.movie.domain.waitingqueue.service;

import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueCommand.ActivateWaitingQueuesCommand;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam.ActivateWaitingQueuesParam;
import com.tdd.movie.domain.waitingqueue.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.tdd.movie.domain.waitingqueue.WaitingQueueConstants.WAITING_QUEUE_EXPIRE_MINUTES;
import static java.util.concurrent.TimeUnit.MINUTES;

@Transactional
@Service
@RequiredArgsConstructor
public class WaitingQueueCommandService {
    private final WaitingQueueRepository waitingQueueRepository;

    // 대기열을 생성한다.
    public String createWaitingQueue() {
        return waitingQueueRepository.addWaitingQueue(UUID.randomUUID().toString());
    }

    // 대기열을 활성화 시킨다.
    public void activateWaitingQueues(ActivateWaitingQueuesCommand command) {
        waitingQueueRepository.activateWaitingQueues(
                new ActivateWaitingQueuesParam(
                        command.availableSlots(),
                        WAITING_QUEUE_EXPIRE_MINUTES,
                        MINUTES
                )
        );
    }

    public void expireOldWaitingQueues(Long seconds) {
        long currentTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long expirationThreshold = currentTimestamp - seconds;

        // 1일 이상 경과한 대기열 토큰 제거
        waitingQueueRepository.removeExpiredWaitingQueues(expirationThreshold);
    }
}
