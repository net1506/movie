package com.tdd.movie.application;

import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDateAfterQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDatePeriodQuery;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.service.MovieQueryService;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetDistinctTheaterIdsByMovieId;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetTheatersByIds;
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

    public List<Movie> getPlayingMovies(LocalDate date) {
        return movieQueryService.getMovies(
                new GetMoviesByDatePeriodQuery(date)
        );
    }

    public List<Movie> getUpcomingMovies(LocalDate date) {
        return movieQueryService.getMovies(
                new GetMoviesByDateAfterQuery(date)
        );
    }

    public List<Theater> getScreeningTheaters(Long movieId) {
        // 영화 코드로 영화관 목록 조회
        List<Long> distinctTheaterIds = theaterQueryService.getDistinctTheaterIds(
                new GetDistinctTheaterIdsByMovieId(movieId)
        );

        // 조회된 영화관 목록이 없는 경우
        if (distinctTheaterIds == null || distinctTheaterIds.isEmpty()) {
            return Collections.emptyList();
        }

        return theaterQueryService.getTheaters(
                new GetTheatersByIds(distinctTheaterIds)
        );
    }
}
