package com.tdd.movie.domain.event;

public interface EventPublisher {

    void publish(String topic, String payload);

    void publish(String topic);

}
