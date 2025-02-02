package com.tdd.movie.domain.movie.dto;

import java.time.LocalDate;

public class MovieQuery {

    public record GetScreeningTheatersByMovieIdQuery(
            Long movieId
    ) {

    }

    public record GetMoviesByDatePeriodQuery(
            LocalDate date
    ) {

    }

    public record GetMoviesByDateAfterQuery(
            LocalDate date
    ) {

    }

}
