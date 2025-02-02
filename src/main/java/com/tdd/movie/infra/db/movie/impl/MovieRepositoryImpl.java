package com.tdd.movie.infra.db.movie.impl;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDateAfterParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDatePeriodParam;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.repository.MovieRepository;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovieRepositoryImpl implements MovieRepository {

    private final MovieJpaRepository movieJpaRepository;

    @Override
    public List<Movie> findMovies(FindMoviesByDatePeriodParam param) {
        return movieJpaRepository.findAllByScreeningPeriod(
                param.date()
        );
    }

    @Override
    public List<Movie> findMovies(FindMoviesByDateAfterParam param) {
        return movieJpaRepository.findAllByScreeningStartDateAfter(param.date());
    }
}
