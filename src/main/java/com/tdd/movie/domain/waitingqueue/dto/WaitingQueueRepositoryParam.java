package com.tdd.movie.domain.waitingqueue.dto;

import java.util.concurrent.TimeUnit;

public class WaitingQueueRepositoryParam {
    public record GetActiveTokenByUuidParam(
            String uuid
    ) {

    }

    public record GetWaitingQueuePositionByUuidParam(
            String uuid
    ) {

    }

    public record ActivateWaitingQueuesParam(
            int availableSlots,
            long timeout,
            TimeUnit unit
    ) {

    }

    public record RemoveActiveTokenParam(
            String uuid
    ) {

    }
}
