package com.tdd.movie.application;

import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.domain.TheaterSeat;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterSeatJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @Autowired
    TheaterSeatJpaRepository theaterSeatJpaRepository;

    @Autowired
    TheaterFacade theaterFacade;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        // 🔥 1. 삭제 순서 조정 (자식 테이블 → 부모 테이블 순으로 삭제)
        theaterSeatJpaRepository.deleteAll();
        theaterScheduleJpaRepository.deleteAll();
        theaterJpaRepository.deleteAll();
        movieJpaRepository.deleteAll();

        // 🔥 2. 트랜잭션 강제 커밋 (flush 호출)
        movieJpaRepository.flush();
        theaterJpaRepository.flush();
        theaterScheduleJpaRepository.flush();
        theaterSeatJpaRepository.flush();

        // 🔥 3. ID 초기화 (AUTO_INCREMENT 문제 해결)
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

            // CGV 강남 & CGV 용산 ID 조회
            Long cgvGangnamId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            Long cgvYongsanId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 용산"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 용산이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangnamId, cgvYongsanId);

            // when
            List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(movieAId, cgvGangnamId);

            // then
            assertThat(reservableTheaterSchedules).hasSize(1);
            assertThat(reservableTheaterSchedules.get(0).getMovieId()).isEqualTo(movieAId);
            assertThat(reservableTheaterSchedules.get(0).getTheaterId()).isEqualTo(cgvGangnamId);
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

            // CGV 강남 & CGV 용산 ID 조회
            Long cgvGangnamId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            Long cgvYongsanId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 용산"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 용산이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangnamId, cgvYongsanId);

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

            // CGV 강남 & CGV 용산 ID 조회
            Long cgvGangnamId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 강남"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 강남이 존재하지 않습니다."));

            Long cgvYongsanId = theaterJpaRepository.findAll().stream()
                    .filter(theater -> theater.getName().equals("CGV 용산"))
                    .map(Theater::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CGV 용산이 존재하지 않습니다."));

            // TheaterSchedule 리스트 생성
            createTheaterSchedule(movieAId, cgvGangnamId, cgvYongsanId);

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
                        .build(),

                Movie.builder()
                        .title("영화 C")
                        .screeningStartDate(LocalDate.now().plusDays(2))
                        .screeningEndDate(LocalDate.now().plusDays(6))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .plot("코미디 영화 C의 줄거리")
                        .posterImageUrl("/images/movies/movie-c.jpg")
                        .runningTime(110)
                        .build(),

                Movie.builder()
                        .title("영화 D")
                        .screeningStartDate(LocalDate.now().plusDays(3))
                        .screeningEndDate(LocalDate.now().plusDays(7))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .plot("스릴러 영화 D의 줄거리")
                        .posterImageUrl("/images/movies/movie-d.jpg")
                        .runningTime(140)
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

    private void createTheaterSchedule(Long movieAId, Long cgvGangnamId, Long cgvYongsanId) {
        List<TheaterSchedule> schedulesInsert = List.of(
                TheaterSchedule.builder()
                        .movieId(movieAId)
                        .theaterId(cgvGangnamId)
                        .theaterScreenId(201L)
                        .movieAt(LocalDateTime.now().plusDays(1).withHour(10)) // 1일 후 오전 10시
                        .reservationStartAt(LocalDateTime.now().minusHours(2)) // 2시간 전부터 예약 가능
                        .reservationEndAt(LocalDateTime.now().plusHours(5)) // 5시간 후 예약 마감
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(movieAId)
                        .theaterId(cgvGangnamId)
                        .theaterScreenId(202L)
                        .movieAt(LocalDateTime.now().plusDays(2).withHour(14)) // 2일 후 오후 2시
                        .reservationStartAt(LocalDateTime.now().plusHours(3)) // 3시간 후 예약 시작
                        .reservationEndAt(LocalDateTime.now().plusHours(6)) // 6시간 후 예약 마감
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(movieAId)
                        .theaterId(cgvYongsanId)
                        .theaterScreenId(203L)
                        .movieAt(LocalDateTime.now().plusDays(3).withHour(18)) // 3일 후 오후 6시
                        .reservationStartAt(LocalDateTime.now().minusHours(4)) // 4시간 전부터 예약 가능
                        .reservationEndAt(LocalDateTime.now().plusHours(7)) // 7시간 후 예약 마감
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(movieAId)
                        .theaterId(cgvYongsanId)
                        .theaterScreenId(204L)
                        .movieAt(LocalDateTime.now().plusDays(4).withHour(20)) // 4일 후 오후 8시
                        .reservationStartAt(LocalDateTime.now().plusHours(5)) // 5시간 후 예약 시작
                        .reservationEndAt(LocalDateTime.now().plusHours(8)) // 8시간 후 예약 마감
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        theaterScheduleJpaRepository.saveAll(schedulesInsert);
    }
}