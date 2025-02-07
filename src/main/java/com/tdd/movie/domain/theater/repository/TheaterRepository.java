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
import static com.tdd.movie.domain.support.CacheName.THEATER_SCHEDULE;

public interface TheaterRepository {

    Reservation saveReservation(Reservation reservation);

    @Cacheable(value = THEATER, key = "#param.theaterId")
    Theater getTheater(GetTheaterByIdParam param);

    List<Theater> findTheaters(FindTheatersByIdsParam param);

    List<Long> findDistinctTheaterIds(FindDistinctTheaterIdsByMovieIdParam param);

    // 콘서트 스케쥴 정보 조회
    @Cacheable(value = THEATER_SCHEDULE, key = "#param.theaterScheduleId")
    TheaterSchedule getTheaterSchedule(GetTheaterScheduleByIdParam param);

    List<TheaterSchedule> findAllTheaterSchedules(FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam param);

    TheaterSeat getTheaterSeat(GetTheaterSeatByIdParam param);

    Reservation getReservation(GetReservationByIdParam param);

    List<TheaterSeat> findAllTheaterSeats(FindAllTheaterSeatsByScheduleIdAndIsReservedParam param);

    List<TheaterSeat> findAllTheaterSeats(FindAllTheaterSeatsByIdsWithLockParam param);

    List<Reservation> findAllReservations(FindAllReservationsByStatusAndReservedAtBeforeWithLockParam param);

    List<Reservation> findAllReservations(FindAllReservationsByIdsWithLockParam param);

}
