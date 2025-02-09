package com.tdd.movie.domain.event.model;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
