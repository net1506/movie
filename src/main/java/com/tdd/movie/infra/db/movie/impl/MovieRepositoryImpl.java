package com.tdd.movie.infra.db.movie.impl;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDateAfterParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDatePeriodParam;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.repository.MovieRepository;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.MOVIE_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class MovieRepositoryImpl implements MovieRepository {

    private final MovieJpaRepository movieJpaRepository;

    @Override
    public Movie getMovie(MovieRepositoryParam.GetMovieByIdParam param) {
        return movieJpaRepository.findById(param.id())
                .orElseThrow(() -> new CoreException(MOVIE_NOT_FOUND));
    }

    @Override
    public List<Movie> findMovies(FindMoviesByDatePeriodParam param) {
        return movieJpaRepository.findAllByScreeningPeriod(
                param.screeningDate()
        );
    }

    @Override
    public List<Movie> findMovies(FindMoviesByDateAfterParam param) {
        return movieJpaRepository.findAllByScreeningStartDateAfter(
                param.screeningDate()
        );
    }
}
