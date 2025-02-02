package com.tdd.movie.domain.theater.dto;

import com.tdd.movie.domain.support.error.CoreException;

import java.util.List;
import java.util.Objects;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.MOVIE_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_EMPTY;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_NULL;

public class TheaterQuery {

    public record GetTheatersByIds(
            List<Long> theaterIds
    ) {
        public GetTheatersByIds {
            if (theaterIds == null) {
                throw new CoreException(THEATER_ID_MUST_NOT_BE_NULL);
            }

            if (theaterIds.isEmpty() || theaterIds.stream().anyMatch(Objects::isNull)) {
                throw new CoreException(THEATER_ID_MUST_NOT_BE_EMPTY);
            }
        }
    }

    public record GetDistinctTheaterIdsByMovieId(
            Long movieId
    ) {
        public GetDistinctTheaterIdsByMovieId {
            if (movieId == null) {
                throw new CoreException(MOVIE_ID_MUST_NOT_BE_NULL);
            }
        }
    }

}
