package com.tdd.movie.domain.movie.dto;

import java.time.LocalDate;

public class MovieRepositoryParam {

    public record FindMoviesByDatePeriodParam(
            LocalDate screeningDate
    ) {

    }

    public record FindDistinctTheaterIdsByMovieIdParam(
            Long movieId
    ) {

    }

    public record FindMoviesByDateAfterParam(
            LocalDate screeningDate
    ) {

    }
}
