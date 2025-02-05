package com.tdd.movie.application;

import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.payment.model.Payment;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.model.Theater;
import com.tdd.movie.domain.theater.model.TheaterSchedule;
import com.tdd.movie.domain.theater.model.TheaterSeat;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import com.tdd.movie.infra.db.theater.ReservationJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterSeatJpaRepository;
import com.tdd.movie.infra.db.user.UserJpaRepository;
import com.tdd.movie.infra.db.user.WalletJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.*;
import static com.tdd.movie.domain.support.error.ErrorType.User.*;
import static com.tdd.movie.domain.theater.model.ReservationStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 테스트 실행 후 롤백 (이전 테스트 데이터 제거)
@DisplayName("TheaterFacade 단위 테스트")
class TheaterFacadeTest {

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

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private WalletJpaRepository walletJpaRepository;

    @BeforeEach
    public void setUp() {
        // 1. 삭제 순서 조정 (자식 테이블 → 부모 테이블 순으로 삭제)
        theaterSeatJpaRepository.deleteAll();
        theaterScheduleJpaRepository.deleteAll();
        theaterJpaRepository.deleteAll();
        movieJpaRepository.deleteAll();

        // 2. 트랜잭션 강제 커밋 (flush 호출)
        movieJpaRepository.flush();
        theaterJpaRepository.flush();
        theaterScheduleJpaRepository.flush();
        theaterSeatJpaRepository.flush();

        // 3. ID 초기화 (AUTO_INCREMENT 문제 해결)
        entityManager.createNativeQuery("ALTER TABLE movies AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE theaters AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE theater_schedules AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE theater_seats AUTO_INCREMENT = 1").executeUpdate();
    }

    @Nested
    @DisplayName("GetReservableTheaterSchedules 단위 테스트")
    class GetReservableTheaterSchedulesTest {
        @Test
        @DisplayName("예약 가능한 영화관 스케쥴 목록 조회 - 데이터가 존재하지 않는 경우")
        public void shouldSuccessGetReservableTheaterSchedulesWhenNoData() throws Exception {
            // given
            createMovieData();

            createTheaterData();

            List<Movie> movies = movieJpaRepository.findAll().stream().filter(movie -> movie.getTitle().equals("영화 A")).toList();
            Long movieId = movies.get(0).getId();

            List<Theater> theaters = theaterJpaRepository.findAll().stream().filter(theater -> theater.getName().equals("CGV 강남")).toList();
            Long theaterId = theaters.get(0).getId();

            // when
            List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(movieId, theaterId);

            // then
            assertThat(reservableTheaterSchedules).hasSize(0);
        }

        @Test
        @DisplayName("예약 가능한 영화관 스케쥴 목록 조회")
        public void shouldSuccessGetReservableTheaterSchedules() throws Exception {
            // given
            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남 ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangNamTheaterId);

            // when
            List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(movieAId, cgvGangNamTheaterId);

            // then
            assertThat(reservableTheaterSchedules).hasSize(1);
            assertThat(reservableTheaterSchedules.get(0).getMovieId()).isEqualTo(movieAId);
            assertThat(reservableTheaterSchedules.get(0).getTheaterId()).isEqualTo(cgvGangNamTheaterId);
        }
    }

    @Nested
    @DisplayName("GetReservableTheaterSchedules 단위 테스트")
    class GetReservableTheaterSeatsTest {
        @Test
        @DisplayName("예약 가능한 영화관 좌석 목록 조회 - 데이터가 존재하지 않는 경우")
        public void shouldSuccessGetReservableTheaterSeatsWhenNoData() throws Exception {
            // given
            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남  ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangNamTheaterId);

            List<Movie> movies = movieJpaRepository.findAll().stream().filter(movie -> movie.getTitle().equals("영화 A")).toList();
            Long movieId = movies.get(0).getId();

            List<Theater> theaters = theaterJpaRepository.findAll().stream().filter(theater -> theater.getName().equals("CGV 강남")).toList();
            Long theaterId = theaters.get(0).getId();
            List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(movieId, theaterId);
            Long theaterScheduleId = reservableTheaterSchedules.get(0).getId();

            // when
            List<TheaterSeat> reservableTheaterSeats = theaterFacade.getReservableTheaterSeats(theaterScheduleId);

            // then
            assertThat(reservableTheaterSeats).hasSize(0);
        }

        @Test
        @DisplayName("예약 가능한 영화관 좌석 목록 조회")
        public void shouldSuccessGetReservableTheaterSeats() throws Exception {
            // given
            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남  ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangNamTheaterId);

            List<Movie> movies = movieJpaRepository.findAll().stream().filter(movie -> movie.getTitle().equals("영화 A")).toList();
            Long movieId = movies.get(0).getId();

            List<Theater> theaters = theaterJpaRepository.findAll().stream().filter(theater -> theater.getName().equals("CGV 강남")).toList();
            Long theaterId = theaters.get(0).getId();
            List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(movieId, theaterId);
            Long theaterScheduleId = reservableTheaterSchedules.get(0).getId();

            // 10개의 좌석 생성
            List<TheaterSeat> seats = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                seats.add(TheaterSeat.builder()
                        .theaterScheduleId(theaterScheduleId)
                        .number(i) // 좌석 번호 1~10
                        .price(10000 + (i * 500)) // 가격 변동 (10,000 + 좌석 번호 * 500)
                        .isReserved(false) // 기본적으로 예약되지 않은 상태
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            // 저장
            theaterSeatJpaRepository.saveAll(seats);

            // when
            List<TheaterSeat> reservableTheaterSeats = theaterFacade.getReservableTheaterSeats(theaterScheduleId);

            // then
            assertThat(reservableTheaterSeats).hasSize(10);
        }
    }

    @Nested
    @DisplayName("영화 예매 내역 저장 테스트")
    class processReservationTest {
        @Test
        @DisplayName("영화 예매 내역 저장 실패 - 사용자가 존재하지 않는 경우")
        public void shouldThrowExceptionWhenUserNotFound() throws Exception {
            // given
            Long userId = 1L;
            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남 ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangNamTheaterId);

            List<Movie> movies = movieJpaRepository.findAll().stream().filter(movie -> movie.getTitle().equals("영화 A")).toList();
            Long movieId = movies.get(0).getId();

            List<Theater> theaters = theaterJpaRepository.findAll().stream().filter(theater -> theater.getName().equals("CGV 강남")).toList();
            Long theaterId = theaters.get(0).getId();
            List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(movieId, theaterId);
            Long theaterScheduleId = reservableTheaterSchedules.get(0).getId();

            // 10개의 좌석 생성
            List<TheaterSeat> seats = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                seats.add(TheaterSeat.builder()
                        .theaterScheduleId(theaterScheduleId)
                        .number(i) // 좌석 번호 1~10
                        .price(10000 + (i * 500)) // 가격 변동 (10,000 + 좌석 번호 * 500)
                        .isReserved(false) // 기본적으로 예약되지 않은 상태
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            // 저장
            List<TheaterSeat> theaterSeats = theaterSeatJpaRepository.saveAll(seats);
            Long theaterSeatId = theaterSeats.get(0).getId();

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processReservation(userId, theaterSeatId));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(USER_NOT_FOUND);
            assertThat(coreException.getMessage()).isEqualTo(USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 저장 실패 - 영화관 좌석이 존재하지 않는 경우")
        public void shouldThrowExceptionWhenTheaterSeatNotFound() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());

            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남  ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangNamTheaterId);

            List<Movie> movies = movieJpaRepository.findAll().stream().filter(movie -> movie.getTitle().equals("영화 A")).toList();
            Long movieId = movies.get(0).getId();

            List<Theater> theaters = theaterJpaRepository.findAll().stream().filter(theater -> theater.getName().equals("CGV 강남")).toList();
            Long theaterId = theaters.get(0).getId();
            List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(movieId, theaterId);
            Long theaterScheduleId = reservableTheaterSchedules.get(0).getId();

            // 10개의 좌석 생성
            List<TheaterSeat> seats = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                seats.add(TheaterSeat.builder()
                        .theaterScheduleId(theaterScheduleId)
                        .number(i) // 좌석 번호 1~10
                        .price(10000 + (i * 500)) // 가격 변동 (10,000 + 좌석 번호 * 500)
                        .isReserved(false) // 기본적으로 예약되지 않은 상태
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            // 저장
            List<TheaterSeat> theaterSeats = theaterSeatJpaRepository.saveAll(seats);
            Long theaterSeatId = 999L;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processReservation(savedUser.getId(), theaterSeatId));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_SEAT_NOT_FOUND);
            assertThat(coreException.getMessage()).isEqualTo(THEATER_SEAT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 저장 실패 - 영화 예매 가능 시간이 아닌 영화 스케쥴을 예약 하려는 경우")
        public void shouldThrowExceptionWhenTheaterScheduleIsNotAvailable() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());

            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남 ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            List<TheaterSchedule> schedulesInsert = List.of(
                    TheaterSchedule.builder()
                            .movieId(movieAId)
                            .theaterId(cgvGangNamTheaterId)
                            .theaterScreenId(201L)
                            .movieAt(LocalDateTime.now().plusDays(1).withHour(10)) // 1일 후 오전 10시
                            .reservationStartAt(LocalDateTime.now().plusDays(1).withHour(1)) // 1일 후 오전 1시
                            .reservationEndAt(LocalDateTime.now().plusDays(1).withHour(5)) // 1일 후 오전 5시
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );

            theaterScheduleJpaRepository.saveAll(schedulesInsert);

            // 10개의 좌석 생성
            List<TheaterSeat> seats = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                seats.add(TheaterSeat.builder()
                        .theaterScheduleId(schedulesInsert.get(0).getId())
                        .number(i) // 좌석 번호 1~10
                        .price(10000 + (i * 500)) // 가격 변동 (10,000 + 좌석 번호 * 500)
                        .isReserved(false) // 기본적으로 예약되지 않은 상태
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            // 저장
            List<TheaterSeat> theaterSeats = theaterSeatJpaRepository.saveAll(seats);
            Long theaterSeatId = theaterSeats.get(0).getId();

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processReservation(savedUser.getId(), theaterSeatId));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_SCHEDULE_NOT_RESERVABLE);
            assertThat(coreException.getMessage()).isEqualTo(THEATER_SCHEDULE_NOT_RESERVABLE.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 저장 실패 - 이미 예약된 영화관 좌석을 예매 하려는 경우")
        public void shouldThrowExceptionWhenTheaterSeatIsNotAvailable() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());

            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남 ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            List<TheaterSchedule> schedulesInsert = List.of(
                    TheaterSchedule.builder()
                            .movieId(movieAId)
                            .theaterId(cgvGangNamTheaterId)
                            .theaterScreenId(201L)
                            .movieAt(LocalDateTime.now().plusDays(1).withHour(10)) // 1일 후 오전 10시
                            .reservationStartAt(LocalDateTime.now().minusDays(1).withHour(1)) // 1일 전 오전 1시
                            .reservationEndAt(LocalDateTime.now().plusDays(1).withHour(23)) // 1일 후 오후 11시
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );

            theaterScheduleJpaRepository.saveAll(schedulesInsert);

            // 10개의 좌석 생성
            List<TheaterSeat> seats = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                seats.add(TheaterSeat.builder()
                        .theaterScheduleId(schedulesInsert.get(0).getId())
                        .number(i) // 좌석 번호 1~10
                        .price(10000 + (i * 500)) // 가격 변동 (10,000 + 좌석 번호 * 500)
                        .isReserved(true) // 기본적으로 예약되지 않은 상태
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            // 저장
            List<TheaterSeat> theaterSeats = theaterSeatJpaRepository.saveAll(seats);
            Long theaterSeatId = theaterSeats.get(0).getId();

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processReservation(savedUser.getId(), theaterSeatId));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_SEAT_ALREADY_RESERVED);
            assertThat(coreException.getMessage()).isEqualTo(THEATER_SEAT_ALREADY_RESERVED.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 저장 성공")
        public void shouldProcessReservation() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());

            createMovieData();

            createTheaterData();

            // 영화 A ID 조회
            Long movieAId = movieJpaRepository.findAll().stream()
                    .filter(movie -> movie.getTitle().equals("영화 A"))
                    .map(Movie::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("영화 A가 존재하지 않습니다."));

            // CGV 강남 ID 조회
            Long cgvGangNamTheaterId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            List<TheaterSchedule> schedulesInsert = List.of(
                    TheaterSchedule.builder()
                            .movieId(movieAId)
                            .theaterId(cgvGangNamTheaterId)
                            .theaterScreenId(201L)
                            .movieAt(LocalDateTime.now().plusDays(1).withHour(10)) // 1일 후 오전 10시
                            .reservationStartAt(LocalDateTime.now().minusDays(1).withHour(1)) // 1일 전 오전 1시
                            .reservationEndAt(LocalDateTime.now().plusDays(1).withHour(23)) // 1일 후 오후 11시
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );

            theaterScheduleJpaRepository.saveAll(schedulesInsert);

            // 10개의 좌석 생성
            List<TheaterSeat> seats = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                seats.add(TheaterSeat.builder()
                        .theaterScheduleId(schedulesInsert.get(0).getId())
                        .number(i) // 좌석 번호 1~10
                        .price(10000 + (i * 500)) // 가격 변동 (10,000 + 좌석 번호 * 500)
                        .isReserved(false) // 기본적으로 예약되지 않은 상태
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            // 저장
            List<TheaterSeat> theaterSeats = theaterSeatJpaRepository.saveAll(seats);
            Long theaterSeatId = theaterSeats.get(0).getId();

            // when
            Reservation reservation = theaterFacade.processReservation(savedUser.getId(), theaterSeatId);

            // then
            assertThat(reservation.getTheaterSeatId()).isEqualTo(theaterSeatId);
            assertThat(reservation.getUserId()).isEqualTo(savedUser.getId());
            assertThat(reservation.getReservedAt()).isBefore(LocalDateTime.now());
            assertThat(reservation.getStatus()).isEqualTo(WAITING);
        }
    }

    @Nested
    @DisplayName("processPayment 단위 테스트")
    class processPaymentTest {

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 사용자가 존재하지 않는 경우")
        public void shouldThrowExceptionWhenUserNotFound() throws Exception {
            // given
            Long userId = null;
            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(userId)
                    .status(WAITING)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(1L)
                    .build());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(userId, savedReservation.getId()));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(USER_NOT_FOUND);
            assertThat(coreException.getMessage()).isEqualTo(USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 영화관 예매 내역이 존재하지 않는 경우")
        public void shouldThrowExceptionWhenReservationNotFound() throws Exception {
            // given
            Long userId = 1L;
            User savedUser = userJpaRepository.save(User.builder().id(userId).name("user-1").build());
            Long reservationId = 1L;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(userId, reservationId));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(RESERVATION_NOT_FOUND);
            assertThat(coreException.getMessage()).isEqualTo(RESERVATION_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 예매 내역의 소유자와 결재자가 상이한 경우")
        public void shouldThrowExceptionWhenReservationOwnerIsWrong() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());
            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(2L)
                    .status(WAITING)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(3L)
                    .build());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(savedUser.getId(), savedReservation.getId()));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(RESERVATION_USER_NOT_MATCHED);
            assertThat(coreException.getMessage()).isEqualTo(RESERVATION_USER_NOT_MATCHED.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 예매 내역의 상태가 이미 결재 확인 상태인 경우")
        public void shouldThrowExceptionWhenPaymentStatusIsConform() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());
            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(savedUser.getId())
                    .status(CONFIRMED)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(3L)
                    .build());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(savedUser.getId(), savedReservation.getId()));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(RESERVATION_ALREADY_PAID);
            assertThat(coreException.getMessage()).isEqualTo(RESERVATION_ALREADY_PAID.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 예매 내역의 상태가 결재 취소 상태인 경우")
        public void shouldThrowExceptionWhenPaymentStatusIsCancel() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());
            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(savedUser.getId())
                    .status(CANCELED)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(3L)
                    .build());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(savedUser.getId(), savedReservation.getId()));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(RESERVATION_ALREADY_CANCELED);
            assertThat(coreException.getMessage()).isEqualTo(RESERVATION_ALREADY_CANCELED.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 예매 좌석이 없는 경우")
        public void shouldThrowExceptionWhenReservationSeatIsNotFound() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());
            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(savedUser.getId())
                    .status(WAITING)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(3L)
                    .build());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(savedUser.getId(), savedReservation.getId()));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_SEAT_NOT_FOUND);
            assertThat(coreException.getMessage()).isEqualTo(THEATER_SEAT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 사용자 지갑이 존재하지 않는 경우")
        public void shouldThrowExceptionWhenWalletIsNotFound() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());
            Integer price = 3000;

            TheaterSeat savedSeat = theaterSeatJpaRepository.save(TheaterSeat.builder()
                    .theaterScheduleId(1L)
                    .price(price)
                    .number(30)
                    .isReserved(false)
                    .build());

            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(savedUser.getId())
                    .status(WAITING)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(savedSeat.getId())
                    .build());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(savedUser.getId(), savedReservation.getId()));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(WALLET_NOT_FOUND);
            assertThat(coreException.getMessage()).isEqualTo(WALLET_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 실패 - 지갑 잔액이 부족한 경우")
        public void shouldThrowExceptionWhenBalanceIsNotEnough() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());
            Wallet savedWallet = walletJpaRepository.save(Wallet.builder()
                    .userId(savedUser.getId())
                    .amount(1000)
                    .build());
            Integer price = 3000;

            TheaterSeat savedSeat = theaterSeatJpaRepository.save(TheaterSeat.builder()
                    .theaterScheduleId(1L)
                    .price(price)
                    .number(30)
                    .isReserved(false)
                    .build());

            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(savedUser.getId())
                    .status(WAITING)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(savedSeat.getId())
                    .build());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterFacade.processPayment(savedUser.getId(), savedReservation.getId()));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(NOT_ENOUGH_BALANCE);
            assertThat(coreException.getMessage()).isEqualTo(NOT_ENOUGH_BALANCE.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 결재 테스트 성공")
        public void shouldSuccessProcessPayment() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().id(1L).name("user-1").build());
            Wallet savedWallet = walletJpaRepository.save(Wallet.builder()
                    .userId(savedUser.getId())
                    .amount(3000)
                    .build());
            Integer price = 3000;

            TheaterSeat savedSeat = theaterSeatJpaRepository.save(TheaterSeat.builder()
                    .theaterScheduleId(1L)
                    .price(price)
                    .number(30)
                    .isReserved(false)
                    .build());

            Reservation savedReservation = reservationJpaRepository.save(Reservation.builder()
                    .userId(savedUser.getId())
                    .status(WAITING)
                    .reservedAt(LocalDateTime.now())
                    .theaterSeatId(savedSeat.getId())
                    .build());

            // when
            Payment payment = theaterFacade.processPayment(savedUser.getId(), savedReservation.getId());
            Wallet fetchedWallet = walletJpaRepository.findById(savedWallet.getId()).orElse(null);
            Reservation reservation = reservationJpaRepository.findById(savedReservation.getId()).orElse(null);

            // then
            assertThat(payment.getAmount()).isEqualTo(price);
            assertThat(payment.getUserId()).isEqualTo(savedUser.getId());
            assertThat(payment.getReservationId()).isEqualTo(savedReservation.getId());
            assertThat(fetchedWallet.getAmount()).isEqualTo(0);
            assertThat(reservation.getStatus()).isEqualTo(CONFIRMED);
        }
    }

    private void createMovieData() {
        List<Movie> moviesInsert = List.of(
                Movie.builder()
                        .title("영화 A")
                        .screeningStartDate(LocalDate.now())
                        .screeningEndDate(LocalDate.now().plusDays(3))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .plot("액션 영화 A의 줄거리")
                        .posterImageUrl("/images/movies/movie-a.jpg")
                        .runningTime(120)
                        .build(),

                Movie.builder()
                        .title("영화 B")
                        .screeningStartDate(LocalDate.now().plusDays(1))
                        .screeningEndDate(LocalDate.now().plusDays(5))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .plot("드라마 영화 B의 줄거리")
                        .posterImageUrl("/images/movies/movie-b.jpg")
                        .runningTime(135)
                        .build()
        );

        // 한 번에 저장
        movieJpaRepository.saveAll(moviesInsert);
    }

    private void createTheaterData() {
        List<Theater> theatersInsert = List.of(
                Theater.builder()
                        .name("CGV 강남")
                        .address("서울특별시 강남구 강남대로 102길 23")
                        .img("/images/theaters/cgv-gangnam.png")
                        .x("37.498095")
                        .y("127.027610")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Theater.builder()
                        .name("CGV 용산")
                        .address("서울특별시 송파구 올림픽로 300 롯데월드몰 5층")
                        .img("/images/theaters/lotte-worldtower.png")
                        .x("37.513272")
                        .y("127.104872")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        theaterJpaRepository.saveAll(theatersInsert);
    }

    private void createTheaterSchedule(Long movieAId, Long cgvGangNamTheaterId) {
        List<TheaterSchedule> schedulesInsert = List.of(
                TheaterSchedule.builder()
                        .movieId(movieAId)
                        .theaterId(cgvGangNamTheaterId)
                        .theaterScreenId(201L)
                        .movieAt(LocalDateTime.now().plusDays(1).withHour(10)) // 1일 후 오전 10시
                        .reservationStartAt(LocalDateTime.now().minusHours(2)) // 2시간 전부터 예약 가능
                        .reservationEndAt(LocalDateTime.now().plusHours(5)) // 5시간 후 예약 마감
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(movieAId)
                        .theaterId(cgvGangNamTheaterId)
                        .theaterScreenId(202L)
                        .movieAt(LocalDateTime.now().plusDays(2).withHour(14)) // 2일 후 오후 2시
                        .reservationStartAt(LocalDateTime.now().plusHours(3)) // 3시간 후 예약 시작
                        .reservationEndAt(LocalDateTime.now().plusHours(6)) // 6시간 후 예약 마감
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        theaterScheduleJpaRepository.saveAll(schedulesInsert);
    }
}