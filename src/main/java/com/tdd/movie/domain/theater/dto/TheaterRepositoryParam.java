package com.tdd.movie.domain.theater.dto;

import com.tdd.movie.domain.theater.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TheaterRepositoryParam {

    public record GetTheaterByIdParam(
            Long theaterId
    ) {

    }

    public record GetTheaterScheduleByIdParam(
            Long theaterScheduleId
    ) {

    }

    public record GetTheaterSeatByIdParam(
            Long theaterSeatId
    ) {

    }

    public record GetReservationByIdParam(
            Long reservationId
    ) {

    }

    public record FindTheatersByIdsParam(
            List<Long> theaterIds
    ) {

    }

    public record FindAllTheaterSeatsByIdsWithLockParam(
            List<Long> theaterSeatIds
    ) {

    }

    public record FindAllTheaterSeatsByScheduleIdAndIsReservedParam(
            Long theaterScheduleId,
            Boolean isReserved
    ) {
    }

    public record FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam(
            Long movieId,
            Long theaterId,
            LocalDateTime now
    ) {

    }

    public record FindAllReservationsByStatusAndReservedAtBeforeWithLockParam(
            ReservationStatus status,
            LocalDateTime expiredAt
    ) {

    }

    public record FindAllReservationsByIdsWithLockParam(
            List<Long> reservationIds
    ) {

    }
}
