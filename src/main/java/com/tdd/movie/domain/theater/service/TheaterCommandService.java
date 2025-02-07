package com.tdd.movie.domain.theater.service;

import com.tdd.movie.domain.theater.dto.TheaterCommand.CancelReservationsByIdsCommand;
import com.tdd.movie.domain.theater.dto.TheaterCommand.CreateReservationCommand;
import com.tdd.movie.domain.theater.dto.TheaterCommand.ReleaseTheaterSeatsByIdsCommand;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindAllReservationsByIdsWithLockParam;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindAllTheaterSeatsByIdsWithLockParam;
import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.model.TheaterSeat;
import com.tdd.movie.domain.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.tdd.movie.domain.theater.model.ReservationStatus.WAITING;

@Service
@RequiredArgsConstructor
@Transactional
public class TheaterCommandService {

    private final TheaterRepository theaterRepository;

    public Reservation createReservation(CreateReservationCommand command) {
        Reservation reservation = Reservation.builder()
                .theaterSeatId(command.theaterSeatId())
                .userId(command.userId())
                .status(WAITING)
                .reservedAt(LocalDateTime.now())
                .build();

        return theaterRepository.saveReservation(reservation);
    }

    public void releaseConcertSeats(ReleaseTheaterSeatsByIdsCommand command) {
        List<TheaterSeat> theaterSeats = theaterRepository.findAllTheaterSeats(
                new FindAllTheaterSeatsByIdsWithLockParam(command.theaterSeatIds())
        );

        theaterSeats.forEach(TheaterSeat::release);
    }

    public void cancelReservations(CancelReservationsByIdsCommand command) {
        List<Reservation> reservations = theaterRepository.findAllReservations(
                new FindAllReservationsByIdsWithLockParam(command.reservationIds()));

        reservations.forEach(Reservation::cancel);
    }
}
