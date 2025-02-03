package com.tdd.movie.domain.movie.dto;

import com.tdd.movie.domain.support.error.CoreException;

import java.time.LocalDate;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.*;

public class MovieQuery {

    public record GetMovieByIdQuery(
            Long movieId
    ) {
        public GetMovieByIdQuery {
            if (movieId == null) {
                throw new CoreException(MOVIE_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record GetScreeningTheatersByMovieIdQuery(
            Long movieId
    ) {
        public GetScreeningTheatersByMovieIdQuery {
            if (movieId == null) {
                throw new CoreException(MOVIE_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record FindPlayingMoviesByDatePeriodQuery(
            LocalDate screeningDate
    ) {
        public FindPlayingMoviesByDatePeriodQuery {
            if (screeningDate == null) {
                throw new CoreException(SCREENING_DATE_MUST_NOT_BE_NULL);
            }
        }
    }

    public record FindUpcomingMoviesByDateAfterQuery(
            LocalDate screeningDate
    ) {
        public FindUpcomingMoviesByDateAfterQuery {
            if (screeningDate == null) {
                throw new CoreException(SCREENING_DATE_MUST_NOT_BE_NULL);
            }

            if (screeningDate.isBefore(LocalDate.now())) {
                throw new CoreException(INVALID_SCREENING_DATE);
            }
        }
    }
}

