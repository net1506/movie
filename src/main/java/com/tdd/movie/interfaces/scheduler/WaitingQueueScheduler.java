package com.tdd.movie.interfaces.scheduler;

import com.tdd.movie.application.WaitingQueueFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매분마다 대기열의 활성화 시키는 스케쥴러를 실행한다.
 */
@Component
@RequiredArgsConstructor
public class WaitingQueueScheduler {

    private final WaitingQueueFacade waitingQueueFacade;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void activateWaitingQueues() {
        waitingQueueFacade.activateWaitingQueues();
    }
}