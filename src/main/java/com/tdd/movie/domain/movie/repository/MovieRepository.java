package com.tdd.movie.domain.movie.repository;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDateAfterParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDatePeriodParam;
import com.tdd.movie.domain.movie.model.Movie;

import java.util.List;

public interface MovieRepository {
    List<Movie> findMovies(FindMoviesByDatePeriodParam param);

    List<Movie> findMovies(FindMoviesByDateAfterParam param);
}
