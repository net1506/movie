package com.tdd.movie.domain.theater.service;

import com.tdd.movie.domain.theater.domain.Reservation;
import com.tdd.movie.domain.theater.dto.TheaterCommand.CreateReservationCommand;
import com.tdd.movie.domain.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.tdd.movie.domain.theater.domain.ReservationStatus.WAITING;

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

}
