package com.tdd.movie.domain.theater.dto;

import com.tdd.movie.domain.support.error.CoreException;

import java.util.List;
import java.util.Objects;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.MOVIE_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.*;

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

    public record GetTheaterScheduleByIdQuery(
            Long theaterScheduleId
    ) {
        public GetTheaterScheduleByIdQuery {
            if (theaterScheduleId == null) {
                throw new CoreException(THEATER_SCHEDULE_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record GetTheaterSeatByIdQuery(
            Long theaterSeatId
    ) {
        public GetTheaterSeatByIdQuery {
            if (theaterSeatId == null) {
                throw new CoreException(THEATER_SEAT_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record GetReservationByIdQuery(
            Long reservationId
    ) {
        public GetReservationByIdQuery {
            if (reservationId == null) {
                throw new CoreException(RESERVATION_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record FindAllExpiredReservationsWithLockQuery() {
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

    public record FindReservableTheaterSeatsQuery(
            Long theaterScheduleId,
            Boolean isReserved
    ) {
        public FindReservableTheaterSeatsQuery {
            if (theaterScheduleId == null) {
                throw new CoreException(THEATER_SCHEDULE_ID_MUST_NOT_BE_NULL);
            }
        }
    }
}
