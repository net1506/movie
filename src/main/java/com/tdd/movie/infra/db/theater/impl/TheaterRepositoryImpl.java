package com.tdd.movie.infra.db.theater.impl;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindTheatersByIdsParam;
import com.tdd.movie.domain.theater.repository.TheaterRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class TheaterRepositoryImpl implements TheaterRepository {

    private final TheaterJpaRepository theaterJpaRepository;
    private final TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @Override
    public Theater getTheater(TheaterRepositoryParam.GetTheaterByIdParam query) {
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
    public List<TheaterSchedule> findAllTheaterSchedules(FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam param) {
        return theaterScheduleJpaRepository.findByTheaterIdAndMovieIdAndReservationPeriod(
                param.theaterId(),
                param.movieId(),
                param.now()
        );
    }
}
