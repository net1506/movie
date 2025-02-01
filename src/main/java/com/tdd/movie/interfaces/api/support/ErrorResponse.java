package com.tdd.movie.interfaces.api.support;

public record ErrorResponse(
        String code,
        String message
) {

}