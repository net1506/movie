package com.tdd.movie.application;

import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.payment.model.Payment;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.model.ReservationStatus;
import com.tdd.movie.domain.theater.model.TheaterSchedule;
import com.tdd.movie.domain.theater.model.TheaterSeat;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import com.tdd.movie.infra.db.payment.PaymentJpaRepository;
import com.tdd.movie.infra.db.theater.ReservationJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterSeatJpaRepository;
import com.tdd.movie.infra.db.user.UserJpaRepository;
import com.tdd.movie.infra.db.user.WalletJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.RESERVATION_ALREADY_PAID;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_SEAT_ALREADY_RESERVED;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("TheaterFacadeConcurrency 단위 테스트")
class TheaterFacadeConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(TheaterFacadeConcurrencyTest.class);

    @Autowired
    MovieJpaRepository movieJpaRepository;

    @Autowired
    TheaterJpaRepository theaterJpaRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @Autowired
    TheaterSeatJpaRepository theaterSeatJpaRepository;

    @Autowired
    TheaterFacade theaterFacade;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Autowired
    WalletJpaRepository walletJpaRepository;

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @BeforeEach
    public void setUp() {
        theaterSeatJpaRepository.deleteAll();
        theaterScheduleJpaRepository.deleteAll();
        theaterJpaRepository.deleteAll();
        movieJpaRepository.deleteAll();
        reservationJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        walletJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("콘서트 좌석 예약 동시성 테스트")
    class ReserveSeatConcurrencyTest {

        @Nested
        @DisplayName("동시성 테스트 - 동일 좌석 동시 예약 낙관적 락")
        class ReserveSeatWithOptimisticLock {

            @Test
            @DisplayName("동시성 테스트 - 동일 좌석 동시 예약")
            void shouldSuccessfullyReserveSeat() {
                // given
                final int threadCount = 1000;
                Movie movie = movieJpaRepository.save(Movie.builder()
                        .title("Movie - 1")
                        .plot("plot")
                        .runningTime(103)
                        .build());

                TheaterSchedule theaterSchedule = theaterScheduleJpaRepository.save(
                        TheaterSchedule.builder()
                                .movieId(movie.getId())
                                .theaterId(1L)
                                .movieAt(LocalDateTime.now().plusDays(2))
                                .reservationStartAt(LocalDateTime.now().minusDays(1))
                                .reservationEndAt(LocalDateTime.now().plusDays(1))
                                .build()
                );

                TheaterSeat theaterSeat = theaterSeatJpaRepository.save(
                        TheaterSeat.builder()
                                .theaterScheduleId(theaterSchedule.getId())
                                .number(1)
                                .isReserved(false)
                                .price(10000)
                                .build()
                );

                List<User> users = IntStream.range(0, threadCount)
                        .mapToObj(i -> userJpaRepository.save(User.builder().name("user" + i).build()))
                        .toList();

                // when
                final List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                        .mapToObj(i -> CompletableFuture.runAsync(() -> {
                            try {
                                theaterFacade.processReservation(users.get(i).getId(), theaterSeat.getId());
                            } catch (ObjectOptimisticLockingFailureException e) {
                                log.error("낙관적 락 충돌 발생! 이미 예매된 좌석입니다.");
                                log.error(e.getMessage());
                                return; // 낙관적 락 충돌 시 즉시 종료
                            } catch (CoreException e) {
                                if (e.getErrorType().equals(THEATER_SEAT_ALREADY_RESERVED)) {
                                    System.out.println("THEATER_SEAT_ALREADY_RESERVED 에러 발생");
                                    return;
                                }

                                throw e;
                            }
                        }))
                        .toList();

                long start = System.currentTimeMillis();

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                long end = System.currentTimeMillis();

                log.info("낙관적 락 Execution Time: " + (end - start) + "ms");

                // then
                final TheaterSeat reservedTheaterSeat = theaterSeatJpaRepository.findById(
                        theaterSeat.getId()).get();
                assertThat(reservedTheaterSeat.getIsReserved()).isTrue();

                final List<Reservation> reservations = reservationJpaRepository.findAll();
                assertThat(reservations).hasSize(1);

                final Reservation reservation = reservations.get(0);
                assertThat(reservation.getTheaterSeatId()).isEqualTo(theaterSeat.getId());
                assertThat(reservation.getUserId()).isNotNull();
                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
            }
        }
    }

    @Nested
    @DisplayName("영화관 좌석 예약 내역 결제 동시성 테스트")
    class PayReservationConcurrencyTest {

        @Nested
        @DisplayName("콘서트 좌석 예약 내역 결제 동시성 테스트 분산락")
        class PayReservationWithDistributedLock {

            @Test
            @DisplayName("동시성 테스트 - 동일 좌석 동시 결제")
            void shouldSuccessfullyPayReservation() {

                // given
                final int threadCount = 1000;

                // 콘서트 스케쥴 생성
                TheaterSchedule theaterSchedule = theaterScheduleJpaRepository.save(
                        TheaterSchedule.builder()
                                .theaterId(1L)
                                .movieAt(LocalDateTime.now().plusDays(1))
                                .reservationStartAt(LocalDateTime.now().minusDays(1))
                                .reservationEndAt(LocalDateTime.now().plusDays(1))
                                .build()
                );

                // 콘서트 좌석 생성
                TheaterSeat theaterSeat = theaterSeatJpaRepository.save(
                        TheaterSeat.builder()
                                .theaterScheduleId(theaterSchedule.getId())
                                .number(1)
                                .isReserved(true)
                                .price(10000)
                                .build()
                );

                // 사용자 생성
                User user = userJpaRepository.save(User.builder().name("user").build());

                // 사용자 지갑 생성
                Wallet userWallet = walletJpaRepository.save(
                        Wallet.builder().userId(user.getId()).amount(10000).build());

                // 콘서트 예약 내역 생성
                Reservation reservation = reservationJpaRepository.save(
                        Reservation.builder()
                                .theaterSeatId(theaterSeat.getId()) // 생성했던 좌석
                                .userId(user.getId()) // user 의 아이디
                                .status(ReservationStatus.WAITING) // 대기
                                .reservedAt(LocalDateTime.now())
                                .build()
                );

                // when
                final List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                        .mapToObj(i -> CompletableFuture.runAsync(() -> {
                            try {
                                // 100 번 동시에 같은 콘서트 예매 내역에 대해서 결재를 시도
                                theaterFacade.processPayment(reservation.getId(), user.getId());
                            } catch (CoreException e) {
                                if (e.getErrorType().equals(RESERVATION_ALREADY_PAID)) {
                                    return;
                                }

                                throw e;
                            }
                        }))
                        .toList();

                long start = System.currentTimeMillis();

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                long end = System.currentTimeMillis();

                log.info("분산 락 Execution Time: " + (end - start) + "ms");

                // then
                final Reservation updatedReservation = reservationJpaRepository.findById(
                                reservation.getId())
                        .get();

                assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

                final List<Payment> payments = paymentJpaRepository.findAll();
                assertThat(payments).hasSize(1); // 총 1 건만 결재됨을 확인

                final Wallet updatedUserWallet = walletJpaRepository.findById(userWallet.getId()).get();
                assertThat(updatedUserWallet.getAmount()).isEqualTo(0); // 남은 금액 확인
            }

            @Test
            @DisplayName("동시성 테스트 - 다른 좌석 같은 사용자 동시 결제 잔액 부족")
            void shouldThrowExceptionWhenPayReservation() {
                // given
                final int threadCount = 100;
                final int canPayCount = 5;
                final int perSeatPrice = 10000;
                TheaterSchedule theaterSchedule = theaterScheduleJpaRepository.save(
                        TheaterSchedule.builder()
                                .theaterId(1L)
                                .movieAt(LocalDateTime.now().plusDays(1))
                                .reservationStartAt(LocalDateTime.now().minusDays(1))
                                .reservationEndAt(LocalDateTime.now().plusDays(1))
                                .build()
                );

                // 총 100개의 좌석을 생성함
                List<TheaterSeat> theaterSeats = IntStream.range(0, threadCount)
                        .mapToObj(i -> theaterSeatJpaRepository.save(
                                TheaterSeat.builder()
                                        .theaterScheduleId(theaterSchedule.getId())
                                        .number(i)
                                        .isReserved(true)
                                        .price(perSeatPrice)
                                        .build()
                        ))
                        .toList();

                User user = userJpaRepository.save(User.builder().name("user").build());

                Wallet userWallet = walletJpaRepository.save(
                        Wallet.builder().userId(user.getId()).amount(perSeatPrice * canPayCount).build());

                // 아직 결재는 하지 않은 100개의 예매건을 생성함
                List<Reservation> reservations = theaterSeats.stream()
                        .map(theaterSeat -> reservationJpaRepository.save(
                                Reservation.builder()
                                        .theaterSeatId(theaterSeat.getId())
                                        .userId(user.getId())
                                        .status(ReservationStatus.WAITING)
                                        .reservedAt(LocalDateTime.now())
                                        .build()
                        ))
                        .toList();

                // when
                final List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                        .mapToObj(i -> CompletableFuture.runAsync(() -> {
                            try {
                                theaterFacade.processPayment(
                                        reservations.get(i).getId(),
                                        user.getId()
                                );
                            } catch (CoreException e) {
                                if (e.getErrorType().equals(ErrorType.User.NOT_ENOUGH_BALANCE)) {
                                    return;
                                }

                                throw e;
                            }
                        }))
                        .toList();

                long start = System.currentTimeMillis();

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                long end = System.currentTimeMillis();

                log.info("분산 락 Execution Time: " + (end - start) + "ms");

                // then
                final List<Reservation> updatedReservations = reservationJpaRepository.findAll();
                final int paidCount = (int) updatedReservations.stream()
                        .filter(reservation -> reservation.getStatus().equals(ReservationStatus.CONFIRMED))
                        .count();
                assertThat(paidCount).isEqualTo(canPayCount);

                final Wallet updatedUserWallet = walletJpaRepository.findById(userWallet.getId()).get();
                assertThat(updatedUserWallet.getAmount()).isEqualTo(0);
            }

            @Test
            @DisplayName("동시성 테스트 - 다른 좌석 같은 사용자 동시 결제")
            void shouldSuccessfullyPayOtherConcertSeatReservation() {
                // given
                final int threadCount = 100;
                final int perSeatPrice = 10000;

                TheaterSchedule theaterSchedule = theaterScheduleJpaRepository.save(
                        TheaterSchedule.builder()
                                .theaterId(1L)
                                .movieAt(LocalDateTime.now().plusDays(1))
                                .reservationStartAt(LocalDateTime.now().minusDays(1))
                                .reservationEndAt(LocalDateTime.now().plusDays(1))
                                .build()
                );

                // 총 100 개의 좌석을 생성
                List<TheaterSeat> concertSeats = IntStream.range(0, threadCount)
                        .mapToObj(i -> theaterSeatJpaRepository.save(
                                TheaterSeat.builder()
                                        .theaterScheduleId(theaterSchedule.getId())
                                        .number(i)
                                        .isReserved(true)
                                        .price(perSeatPrice)
                                        .build()
                        ))
                        .toList();

                User user = userJpaRepository.save(User.builder().name("user").build());

                // 100(좌석 금액) * 100(좌석 갯수) = 10000 금액 충전
                Wallet userWallet = walletJpaRepository.save(
                        Wallet.builder().userId(user.getId()).amount(perSeatPrice * threadCount).build());

                // 100개의 좌석 모두 예매
                List<Reservation> reservations = concertSeats.stream()
                        .map(concertSeat -> reservationJpaRepository.save(
                                Reservation.builder()
                                        .theaterSeatId(concertSeat.getId())
                                        .userId(user.getId())
                                        .status(ReservationStatus.WAITING)
                                        .reservedAt(LocalDateTime.now())
                                        .build()
                        ))
                        .toList();

                // when
                final List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                        // 100개의 예매 내역건에 대해서 비동기로 100번 결재
                        .mapToObj(i -> CompletableFuture.runAsync(() -> {
                            try {
                                theaterFacade.processPayment(
                                        reservations.get(i).getId(),
                                        user.getId()
                                );
                            } catch (CoreException e) {
                                if (e.getErrorType().equals(ErrorType.User.NOT_ENOUGH_BALANCE)) {
                                    return;
                                }

                                throw e;
                            }
                        }))
                        .toList();

                long start = System.currentTimeMillis();

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                long end = System.currentTimeMillis();

                log.info("분산락 락 Execution Time: " + (end - start) + "ms");

                // then
                final List<Reservation> updatedReservations = reservationJpaRepository.findAll();
                final int paidCount = (int) updatedReservations.stream()
                        .filter(reservation -> reservation.getStatus().equals(ReservationStatus.CONFIRMED))
                        .count();
                assertThat(paidCount).isEqualTo(threadCount);

                final Wallet updatedUserWallet = walletJpaRepository.findById(userWallet.getId()).get();
                assertThat(updatedUserWallet.getAmount()).isEqualTo(0);
            }
        }
    }
}