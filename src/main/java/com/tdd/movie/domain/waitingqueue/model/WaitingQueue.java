package com.tdd.movie.domain.waitingqueue.model;

import com.tdd.movie.domain.support.error.CoreException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.tdd.movie.domain.support.error.ErrorType.WaitingQueue.WAITING_QUEUE_EXPIRED;
import static java.time.LocalDateTime.now;

@Getter
@NoArgsConstructor
public class WaitingQueue {

    private String uuid;

    private LocalDateTime expiredAt;

    @Builder
    public WaitingQueue(String uuid, LocalDateTime expiredAt) {
        this.uuid = uuid;
        this.expiredAt = expiredAt;
    }

    public void validateProcessing() {
        if (now().isAfter(expiredAt)) {
            throw new CoreException(WAITING_QUEUE_EXPIRED);
        }
    }
}
