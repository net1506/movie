package com.tdd.movie.infra.db.theater.impl;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindAllTheaterSeatsByScheduleIdAndIsReservedParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.domain.TheaterSeat;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindTheatersByIdsParam;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.GetTheaterByIdParam;
import com.tdd.movie.domain.theater.repository.TheaterRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterSeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_NOT_FOUND;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_SCHEDULE_ID_MUST_NOT_BE_NULL;

@Repository
@RequiredArgsConstructor
public class TheaterRepositoryImpl implements TheaterRepository {

    private final TheaterJpaRepository theaterJpaRepository;
    private final TheaterScheduleJpaRepository theaterScheduleJpaRepository;
    private final TheaterSeatJpaRepository theaterSeatJpaRepository;

    @Override
    public Theater getTheater(GetTheaterByIdParam query) {
        return theaterJpaRepository.findById(query.theaterId())
                .orElseThrow(() -> new CoreException(THEATER_NOT_FOUND));
    }

    @Override
    public List<Theater> findTheaters(FindTheatersByIdsParam param) {
        return theaterJpaRepository.findAllById(param.theaterIds());
    }

    @Override
    public List<Long> findDistinctTheaterIds(FindDistinctTheaterIdsByMovieIdParam param) {
        return theaterScheduleJpaRepository.findDistinctTheaterIdsByMovieId(param.movieId());
    }

    @Override
    public TheaterSchedule getTheaterSchedule(TheaterRepositoryParam.GetTheaterScheduleByIdParam query) {
        return theaterScheduleJpaRepository.findById(query.theaterScheduleId())
                .orElseThrow(() -> new CoreException(THEATER_SCHEDULE_ID_MUST_NOT_BE_NULL));
    }

    @Override
    public List<TheaterSchedule> findAllTheaterSchedules(FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam param) {
        return theaterScheduleJpaRepository.findByTheaterIdAndMovieIdAndReservationPeriod(
                param.theaterId(),
                param.movieId(),
                param.now()
        );
    }

    @Override
    public List<TheaterSeat> findAllTheaterSeats(FindAllTheaterSeatsByScheduleIdAndIsReservedParam param) {
        return theaterSeatJpaRepository.findAllByTheaterScheduleIdAndIsReserved(
                param.theaterScheduleId(),
                param.isReserved()
        );
    }
}
