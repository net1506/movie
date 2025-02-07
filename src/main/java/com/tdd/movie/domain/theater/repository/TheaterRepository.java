package com.tdd.movie.domain.theater.repository;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.*;
import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.model.Theater;
import com.tdd.movie.domain.theater.model.TheaterSchedule;
import com.tdd.movie.domain.theater.model.TheaterSeat;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

import static com.tdd.movie.domain.support.CacheName.THEATER;

public interface TheaterRepository {

    Reservation saveReservation(Reservation reservation);

    @Cacheable(value = THEATER, key = "#query.theaterId")
    Theater getTheater(GetTheaterByIdParam query);

    List<Theater> findTheaters(FindTheatersByIdsParam param);

    List<Long> findDistinctTheaterIds(FindDistinctTheaterIdsByMovieIdParam param);

    TheaterSchedule getTheaterSchedule(GetTheaterScheduleByIdParam query);

    List<TheaterSchedule> findAllTheaterSchedules(FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam param);

    TheaterSeat getTheaterSeat(GetTheaterSeatByIdParam query);

    Reservation getReservation(GetReservationByIdParam query);

    List<TheaterSeat> findAllTheaterSeats(FindAllTheaterSeatsByScheduleIdAndIsReservedParam param);

    List<TheaterSeat> findAllTheaterSeats(FindAllTheaterSeatsByIdsWithLockParam param);

    List<Reservation> findAllReservations(FindAllReservationsByStatusAndReservedAtBeforeWithLockParam param);

    List<Reservation> findAllReservations(FindAllReservationsByIdsWithLockParam param);

}
