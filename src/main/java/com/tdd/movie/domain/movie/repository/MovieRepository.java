package com.tdd.movie.domain.movie.repository;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDateAfterParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDatePeriodParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.GetMovieByIdParam;
import com.tdd.movie.domain.movie.model.Movie;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

import static com.tdd.movie.domain.support.CacheName.MOVIE;

public interface MovieRepository {
    @Cacheable(value = MOVIE, key = "#param.id")
    Movie getMovie(GetMovieByIdParam param);

    List<Movie> findMovies(FindMoviesByDatePeriodParam param);

    List<Movie> findMovies(FindMoviesByDateAfterParam param);
}
