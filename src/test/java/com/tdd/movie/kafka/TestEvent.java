package com.tdd.movie.kafka;


import com.tdd.movie.domain.support.Event;

import static com.tdd.movie.domain.support.EventType.MY_TOPIC;

public class TestEvent implements Event {

    private final String payload;

    public TestEvent(String payload) {
        this.payload = payload;
    }

    @Override
    public String getType() {
        return MY_TOPIC;
    }

    @Override
    public String getPayload() {
        return payload;
    }

}
