package com.tdd.movie.application;

import com.tdd.movie.domain.movie.dto.MovieQuery.GetMovieByIdQuery;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.service.MovieQueryService;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.dto.TheaterQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetTheaterByIdQuery;
import com.tdd.movie.domain.theater.service.TheaterQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TheaterFacade {

    private final MovieQueryService movieQueryService;

    private final TheaterQueryService theaterQueryService;

    /**
     * 예약 가능한 영화 스케쥴 목록 반환
     *
     * @return
     */
    public List<TheaterSchedule> getReservableTheaterSchedules(
            Long theaterId,
            Long movieId
    ) {
        // 영화 정보를 불러온다.
        Movie movie = movieQueryService.getMovie(new GetMovieByIdQuery(movieId));

        // 영화관 정보를 불러온다.
        Theater theater = theaterQueryService.getTheater(new GetTheaterByIdQuery(theaterId));

        // 이용 가능한 영화관 스케쥴 목록을 반환한다.
        return theaterQueryService.findReservableTheaterSchedules(
                new TheaterQuery.FindReservableTheaterSchedulesQuery(
                        theater.getId(),
                        movie.getId()
                )
        );
    }

}
