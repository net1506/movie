package com.tdd.movie.domain.theater.dto;

import com.tdd.movie.domain.support.error.CoreException;

import java.util.List;
import java.util.Objects;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.MOVIE_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_EMPTY;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_NULL;

public class TheaterQuery {

    public record GetTheaterByIdQuery(
            Long theaterId
    ) {
        public GetTheaterByIdQuery {
            if (theaterId == null) {
                throw new CoreException(THEATER_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record FindTheatersByIdsQuery(
            List<Long> theaterIds
    ) {
        public FindTheatersByIdsQuery {
            if (theaterIds == null) {
                throw new CoreException(THEATER_ID_MUST_NOT_BE_NULL);
            }

            if (theaterIds.isEmpty() || theaterIds.stream().anyMatch(Objects::isNull)) {
                throw new CoreException(THEATER_ID_MUST_NOT_BE_EMPTY);
            }
        }
    }

    public record FindDistinctTheaterIdsByMovieIdQuery(
            Long movieId
    ) {
        public FindDistinctTheaterIdsByMovieIdQuery {
            if (movieId == null) {
                throw new CoreException(MOVIE_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record FindReservableTheaterSchedulesQuery(
            Long theaterId,
            Long movieId
    ) {
        public FindReservableTheaterSchedulesQuery {
            if (theaterId == null) {
                throw new CoreException(THEATER_ID_MUST_NOT_BE_NULL);
            }
            
            if (movieId == null) {
                throw new CoreException(MOVIE_ID_MUST_NOT_BE_NULL);
            }
        }
    }
}
