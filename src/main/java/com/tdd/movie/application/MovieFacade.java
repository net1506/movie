package com.tdd.movie.application;

import com.tdd.movie.domain.movie.dto.MovieQuery.FindPlayingMoviesByDatePeriodQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.FindUpcomingMoviesByDateAfterQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.GetMovieByIdQuery;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.service.MovieQueryService;
import com.tdd.movie.domain.theater.model.Theater;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindDistinctTheaterIdsByMovieIdQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindTheatersByIdsQuery;
import com.tdd.movie.domain.theater.service.TheaterQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieFacade {

    private final MovieQueryService movieQueryService;

    private final TheaterQueryService theaterQueryService;

    public Movie getMovie(Long movieId) {
        return movieQueryService.getMovie(new GetMovieByIdQuery(movieId));
    }

    public List<Movie> getPlayingMovies(LocalDate date) {
        return movieQueryService.findPlayingMovies(
                new FindPlayingMoviesByDatePeriodQuery(date)
        );
    }

    public List<Movie> getUpcomingMovies(LocalDate date) {
        return movieQueryService.findUpcomingMovies(
                new FindUpcomingMoviesByDateAfterQuery(date)
        );
    }

    public List<Theater> getScreeningTheaters(Long movieId) {
        // 영화 코드로 영화관 목록 조회
        List<Long> distinctTheaterIds = theaterQueryService.findDistinctTheaterIds(
                new FindDistinctTheaterIdsByMovieIdQuery(movieId)
        );

        // 조회된 영화관 목록이 없는 경우
        if (distinctTheaterIds == null || distinctTheaterIds.isEmpty()) {
            return Collections.emptyList();
        }

        return theaterQueryService.findTheaters(
                new FindTheatersByIdsQuery(distinctTheaterIds)
        );
    }
}
