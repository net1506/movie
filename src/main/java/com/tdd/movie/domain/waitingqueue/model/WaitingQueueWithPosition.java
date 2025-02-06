package com.tdd.movie.domain.waitingqueue.model;

public record WaitingQueueWithPosition(
        String uuid,
        Long position
) {

}