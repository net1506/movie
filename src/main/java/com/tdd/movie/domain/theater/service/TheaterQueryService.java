package com.tdd.movie.domain.theater.service;


import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindAllTheaterSeatsByScheduleIdAndIsReservedParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.theater.domain.Reservation;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.domain.TheaterSeat;
import com.tdd.movie.domain.theater.dto.TheaterQuery.*;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.*;
import com.tdd.movie.domain.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterQueryService {

    private final TheaterRepository theaterRepository;

    public Theater getTheater(GetTheaterByIdQuery query) {
        return theaterRepository.getTheater(new GetTheaterByIdParam(query.theaterId()));
    }

    public List<Theater> findTheaters(FindTheatersByIdsQuery query) {
        return theaterRepository.findTheaters(
                new FindTheatersByIdsParam(query.theaterIds())
        );
    }

    public List<Long> findDistinctTheaterIds(FindDistinctTheaterIdsByMovieIdQuery query) {
        return theaterRepository.findDistinctTheaterIds(
                new FindDistinctTheaterIdsByMovieIdParam(query.movieId())
        );
    }

    public TheaterSchedule getTheaterSchedule(GetTheaterScheduleByIdQuery query) {
        return theaterRepository.getTheaterSchedule(new GetTheaterScheduleByIdParam(query.theaterScheduleId()));
    }

    public List<TheaterSchedule> findReservableTheaterSchedules(FindReservableTheaterSchedulesQuery query) {
        return theaterRepository.findAllTheaterSchedules(
                new FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam(
                        query.movieId(),
                        query.theaterId(),
                        now()
                )
        );
    }

    public TheaterSeat getTheaterSeat(GetTheaterSeatByIdQuery query) {
        return theaterRepository.getTheaterSeat(new GetTheaterSeatByIdParam(query.theaterSeatId()));
    }

    public List<TheaterSeat> findReservableTheaterSeats(FindReservableTheaterSeatsQuery query) {
        return theaterRepository.findAllTheaterSeats(
                new FindAllTheaterSeatsByScheduleIdAndIsReservedParam(
                        query.theaterScheduleId(),
                        query.isReserved()
                )
        );
    }

    public Reservation getReservation(GetReservationByIdQuery query) {
        return theaterRepository.getReservation(new GetReservationByIdParam(query.reservationId()));
    }
}
