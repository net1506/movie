package com.tdd.movie.domain.movie.service;


import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDateAfterQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDatePeriodQuery;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDateAfterParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindMoviesByDatePeriodParam;
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

    public List<Movie> getMovies(GetMoviesByDatePeriodQuery query) {
        return movieRepository.findMovies(new FindMoviesByDatePeriodParam(query.date()));
    }

    public List<Movie> getMovies(GetMoviesByDateAfterQuery query) {
        return movieRepository.findMovies(new FindMoviesByDateAfterParam(query.date()));
    }
}
