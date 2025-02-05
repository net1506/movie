package com.tdd.movie.application;

import com.tdd.movie.domain.movie.dto.MovieQuery.GetMovieByIdQuery;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.service.MovieQueryService;
import com.tdd.movie.domain.theater.domain.Reservation;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.domain.TheaterSeat;
import com.tdd.movie.domain.theater.dto.TheaterCommand.CreateReservationCommand;
import com.tdd.movie.domain.theater.dto.TheaterQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.*;
import com.tdd.movie.domain.theater.service.TheaterCommandService;
import com.tdd.movie.domain.theater.service.TheaterQueryService;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserByIdQuery;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TheaterFacade {

    private final UserQueryService userQueryService;

    private final MovieQueryService movieQueryService;

    private final TheaterQueryService theaterQueryService;

    private final TheaterCommandService theaterCommandService;


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

    /**
     * 예약 가능한 영화 좌석 목록 반환
     *
     * @return
     */
    public List<TheaterSeat> getReservableTheaterSeats(
            Long theaterScheduleId
    ) {
        // 영화 스케쥴 정보를 불러온다.
        TheaterSchedule theaterSchedule = theaterQueryService.getTheaterSchedule(new GetTheaterScheduleByIdQuery(theaterScheduleId));

        // 영화 정보를 불러온다.
        movieQueryService.getMovie(new GetMovieByIdQuery(theaterSchedule.getMovieId()));

        // 영화관 정보를 불러온다.
        theaterQueryService.getTheater(new GetTheaterByIdQuery(theaterSchedule.getTheaterId()));

        // 이용 가능한 영화관 스케쥴 목록을 반환한다.
        return theaterQueryService.findReservableTheaterSeats(
                new FindReservableTheaterSeatsQuery(
                        theaterSchedule.getId(),
                        false
                )
        );
    }

    /**
     * 영화관 좌석을 예매한다.
     *
     * @return Reservation 객체
     */
    public Reservation processReservation(Long userId, Long theaterSeatId) {
        User user = userQueryService.getUser(new GetUserByIdQuery(userId));

        TheaterSeat theaterSeat = theaterQueryService.getTheaterSeat(new GetTheaterSeatByIdQuery(theaterSeatId));
        
        TheaterSchedule theaterSchedule = theaterQueryService.getTheaterSchedule(new GetTheaterScheduleByIdQuery(theaterSeat.getTheaterScheduleId()));

        // 영화 조회 ( 존재 하는 영화 인지 에러 체크를 위한 조회 )
        movieQueryService.getMovie(new GetMovieByIdQuery(theaterSchedule.getMovieId()));

        // 예약이 가능한 기간인지 검증
        theaterSchedule.validateReservablePeriod();

        // 영화관 좌석의 예매 가능 여부를 확인 후 예매 상태로 변경
        theaterSeat.reserve();

        // 영화 예매 내역을 저장한다.
        Reservation reservation = theaterCommandService.createReservation(new CreateReservationCommand(theaterSeat.getId(), user.getId()));

        return theaterQueryService.getReservation(new GetReservationByIdQuery(reservation.getId()));
    }

}
