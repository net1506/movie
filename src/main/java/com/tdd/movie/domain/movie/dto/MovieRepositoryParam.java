package com.tdd.movie.domain.movie.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovieRepositoryParam {

    public record GetMovieByIdParam(
            Long id
    ) {

    }

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

    public record FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam(
            Long movieId,
            Long theaterId,
            LocalDateTime now
    ) {

    }

    public record FindAllTheaterSeatsByScheduleIdAndIsReservedParam(
            Long theaterScheduleId,
            Boolean isReserved
    ) {

    }
}
