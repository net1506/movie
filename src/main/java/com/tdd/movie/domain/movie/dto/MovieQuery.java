package com.tdd.movie.domain.movie.dto;

import com.tdd.movie.domain.support.error.CoreException;

import java.time.LocalDate;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.MOVIE_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.Movie.SCREEN_DATE_MUST_NOT_BE_NULL;

public class MovieQuery {

    public record GetScreeningTheatersByMovieIdQuery(
            Long movieId
    ) {
        public GetScreeningTheatersByMovieIdQuery {
            if (movieId == null) {
                throw new CoreException(MOVIE_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record GetMoviesByDatePeriodQuery(
            LocalDate date
    ) {
        public GetMoviesByDatePeriodQuery {
            if (date == null) {
                throw new CoreException(SCREEN_DATE_MUST_NOT_BE_NULL);
            }
        }
    }

    public record GetMoviesByDateAfterQuery(
            LocalDate date
    ) {
        public GetMoviesByDateAfterQuery {
            if (date == null) {
                throw new CoreException(SCREEN_DATE_MUST_NOT_BE_NULL);
            }
        }
    }
}

