package com.tdd.movie.domain.theater.dto;

import com.tdd.movie.domain.support.error.CoreException;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_SEAT_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.User.USER_ID_MUST_NOT_BE_NULL;

public class TheaterCommand {
    public record CreateReservationCommand(
            Long theaterSeatId,
            Long userId
    ) {
        public CreateReservationCommand {
            if (theaterSeatId == null) {
                throw new CoreException(THEATER_SEAT_ID_MUST_NOT_BE_NULL);
            }

            if (userId == null) {
                throw new CoreException(USER_ID_MUST_NOT_BE_NULL);
            }
        }
    }
}
