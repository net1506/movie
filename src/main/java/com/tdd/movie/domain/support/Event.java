package com.tdd.movie.domain.support;

public interface Event {

    String getType();

    String getPayload();

}