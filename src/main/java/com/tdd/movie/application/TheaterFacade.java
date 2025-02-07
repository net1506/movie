package com.tdd.movie.application;

import com.tdd.movie.domain.movie.dto.MovieQuery.GetMovieByIdQuery;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.movie.service.MovieQueryService;
import com.tdd.movie.domain.payment.dto.PaymentCommand.CreatePaymentCommand;
import com.tdd.movie.domain.payment.dto.PaymentQuery.GetPaymentByIdQuery;
import com.tdd.movie.domain.payment.model.Payment;
import com.tdd.movie.domain.payment.service.PaymentCommandService;
import com.tdd.movie.domain.payment.service.PaymentQueryService;
import com.tdd.movie.domain.support.annotaion.DistributedLock;
import com.tdd.movie.domain.theater.dto.TheaterCommand.CancelReservationsByIdsCommand;
import com.tdd.movie.domain.theater.dto.TheaterCommand.CreateReservationCommand;
import com.tdd.movie.domain.theater.dto.TheaterCommand.ReleaseTheaterSeatsByIdsCommand;
import com.tdd.movie.domain.theater.dto.TheaterQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.*;
import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.model.Theater;
import com.tdd.movie.domain.theater.model.TheaterSchedule;
import com.tdd.movie.domain.theater.model.TheaterSeat;
import com.tdd.movie.domain.theater.service.TheaterCommandService;
import com.tdd.movie.domain.theater.service.TheaterQueryService;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetWalletByUserIdQuery;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tdd.movie.domain.support.DistributedLockType.USER_WALLET;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterFacade {

    private final UserQueryService userQueryService;

    private final MovieQueryService movieQueryService;

    private final TheaterQueryService theaterQueryService;

    private final TheaterCommandService theaterCommandService;

    private final PaymentCommandService paymentCommandService;

    private final PaymentQueryService paymentQueryService;


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
    @Transactional
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

    /**
     * 영화관 좌석을 결재한다.
     *
     * @return Payment 객체
     */
    @DistributedLock(type = USER_WALLET, keys = "userId")
    @Transactional
    public Payment processPayment(Long reservationId, Long userId) {
        User user = userQueryService.getUser(new GetUserByIdQuery(userId));
        Reservation reservation = theaterQueryService.getReservation(new GetReservationByIdQuery(reservationId));

        // 예매 내역의 소유자와 결재자 동일 여부 확인
        reservation.validateReservationOwner(user.getId());

        // 예매 내역의 상태를 체크한다. (취소된 상태는 아닌지 또는 이미 결재된 상태는 아닌지 체크)
        reservation.validatePaymentStatus();

        TheaterSeat theaterSeat = theaterQueryService.getTheaterSeat(new GetTheaterSeatByIdQuery(reservation.getTheaterSeatId()));
        Integer price = theaterSeat.getPrice(); // 결제 금액

        Wallet wallet = userQueryService.getWallet(new GetWalletByUserIdQuery(userId));
        // 잔액 여부 확인 및 결재 처리
        wallet.use(price);

        reservation.confirm();

        // 결재 내역을 저장한다.
        Long paymentId = paymentCommandService.createPayment(new CreatePaymentCommand(reservationId, userId, price));

        return paymentQueryService.getPayment(new GetPaymentByIdQuery(paymentId));
    }

    @Transactional
    public void expireReservations() {
        // 현재 시간 기준 만료된 예약 건에 대해서 조회를 한다.
        // RESERVATION_EXPIRATION_MINUTES(5분) 이 지나도 결재가 되지 않는 예약 목록 건을 조회
        List<Reservation> expiredReservations = theaterQueryService.findAllExpiredReservations(
                new FindAllExpiredReservationsWithLockQuery()
        );

        // 없으면 그냥 함수 종료
        if (expiredReservations.isEmpty()) {
            return;
        }

        // 만료된 예약 건에 대한 좌석 아이디 목록 리스트
        List<Long> theaterSeatIds = expiredReservations.stream()
                .map(Reservation::getTheaterSeatId)
                .toList();
        // 만료된 예약 건에 대한 예약 아이디 목록 리스트
        List<Long> reservationIds = expiredReservations.stream()
                .map(Reservation::getId)
                .toList();
        // 좌석을 취소한다.
        theaterCommandService.releaseConcertSeats(new ReleaseTheaterSeatsByIdsCommand(theaterSeatIds));
        // 예약을 취소한다.
        theaterCommandService.cancelReservations(new CancelReservationsByIdsCommand(reservationIds));
    }
}
