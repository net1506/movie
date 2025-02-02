package com.tdd.movie.domain.theater.repository;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindTheatersByIds;

import java.util.List;

public interface TheaterRepository {
    List<Theater> findTheaters(FindTheatersByIds param);

    List<Long> findDistinctTheaterIds(FindDistinctTheaterIdsByMovieIdParam param);
}
