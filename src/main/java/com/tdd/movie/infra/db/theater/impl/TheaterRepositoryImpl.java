package com.tdd.movie.infra.db.theater.impl;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindTheatersByIds;
import com.tdd.movie.domain.theater.repository.TheaterRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TheaterRepositoryImpl implements TheaterRepository {

    private final TheaterJpaRepository theaterJpaRepository;
    private final TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @Override
    public List<Theater> findTheaters(FindTheatersByIds param) {
        return theaterJpaRepository.findAllById(param.theaterIds());
    }

    @Override
    public List<Long> findDistinctTheaterIds(FindDistinctTheaterIdsByMovieIdParam param) {
        return theaterScheduleJpaRepository.findDistinctTheaterIdsByMovieId(param.movieId());
    }
}
