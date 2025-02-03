package com.tdd.movie.domain.movie.service;


import com.tdd.movie.domain.movie.dto.MovieQuery.FindPlayingMoviesByDatePeriodQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.FindUpcomingMoviesByDateAfterQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.GetMovieByIdQuery;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDateAfterParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDatePeriodParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.GetMovieByIdParam;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryService {

    private final MovieRepository movieRepository;

    public Movie getMovie(GetMovieByIdQuery query) {
        return movieRepository.getMovie(new GetMovieByIdParam(query.movieId()));
    }

    public List<Movie> findPlayingMovies(FindPlayingMoviesByDatePeriodQuery query) {
        return movieRepository.findMovies(new FindMoviesByDatePeriodParam(query.screeningDate()));
    }

    public List<Movie> findUpcomingMovies(FindUpcomingMoviesByDateAfterQuery query) {
        return movieRepository.findMovies(new FindMoviesByDateAfterParam(query.screeningDate()));
    }
}
