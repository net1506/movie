package com.tdd.movie.domain.theater.service;

import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.model.Theater;
import com.tdd.movie.domain.theater.model.TheaterSchedule;
import com.tdd.movie.domain.theater.model.TheaterSeat;
import com.tdd.movie.domain.theater.dto.TheaterQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindDistinctTheaterIdsByMovieIdQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindReservableTheaterSchedulesQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindTheatersByIdsQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetTheaterByIdQuery;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterSeatJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@DisplayName("TheaterQueryService 단위 테스트")
class TheaterQueryServiceTest {

    @Autowired
    TheaterQueryService theaterQueryService;

    @Autowired
    MovieJpaRepository movieJpaRepository;

    @Autowired
    TheaterJpaRepository theaterJpaRepository;

    @Autowired
    TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @Autowired
    TheaterSeatJpaRepository theaterSeatJpaRepository;

    @BeforeEach
    public void setUp() {
        movieJpaRepository.deleteAll();
        theaterJpaRepository.deleteAll();
        theaterScheduleJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("영화관 목록 조회 Test")
    class getTheatersTest {

        @Test
        @DisplayName("영화관 목록 조회 실패 - threadIds 가 NULL 인 경우")
        public void shouldThrowExceptionWhenTheadIdsIsNull() throws Exception {
            // given
            List<Long> threadIds = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterQueryService.findTheaters(new FindTheatersByIdsQuery(threadIds)));

            // then
            assertThat(coreException.getMessage()).isEqualTo(THEATER_ID_MUST_NOT_BE_NULL.getMessage());
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_ID_MUST_NOT_BE_NULL);
        }

        @Test
        @DisplayName("영화관 목록 조회 실패 - threadIds 가 Empty 인 경우")
        public void shouldThrowExceptionWhenTheadIdsIsEmpty() throws Exception {
            // given
            List<Long> threadIds = new ArrayList<>();

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterQueryService.findTheaters(new FindTheatersByIdsQuery(threadIds)));

            // then
            assertThat(coreException.getMessage()).isEqualTo(THEATER_ID_MUST_NOT_BE_EMPTY.getMessage());
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_ID_MUST_NOT_BE_EMPTY);
        }

        @Test
        @DisplayName("영화관 목록 조회 실패 - 조회 데이터가 비어 있는 경우")
        public void shouldThrowExceptionWhenTheaterIsNotFound() throws Exception {
            // given
            List<Long> threadIds = List.of(1L, 2L, 3L);

            // when
            List<Theater> theaters = theaterQueryService.findTheaters(new FindTheatersByIdsQuery(threadIds));

            // then
            assertThat(theaters).hasSize(0);
            assertThat(theaters).isEqualTo(Collections.emptyList());
        }

        @Test
        @DisplayName("영화관 목록 조회 성공")
        public void shouldGetTheatersByIds() throws Exception {
            // given
            Theater savedTheater = theaterJpaRepository.save(
                    Theater.builder()
                            .name("신도림역")
                            .address("신도림역 주소")
                            .build()
            );
            List<Long> threadIds = List.of(savedTheater.getId());

            // when
            List<Theater> theaters = theaterQueryService.findTheaters(new FindTheatersByIdsQuery(threadIds));

            // then
            assertThat(theaters).hasSize(1);
            assertThat(theaters.get(0).getName()).isEqualTo("신도림역");
            assertThat(theaters.get(0).getAddress()).isEqualTo("신도림역 주소");
        }
    }

    @Nested
    @DisplayName("중복 제거된 영화관 ID 목록 조회 Test")
    class GetDistinctTheaterIdsTest {
        @Test
        @DisplayName("영화관 목록 조회 실패 - 조회 데이터가 비어 있는 경우")
        public void shouldThrowExceptionWhenTheaterIsNotFound() throws Exception {
            // given
            Long movieId = 1L;

            // when
            List<Long> distinctTheaterIds = theaterQueryService.findDistinctTheaterIds(new FindDistinctTheaterIdsByMovieIdQuery(movieId));

            // then
            assertThat(distinctTheaterIds).hasSize(0);
            assertThat(distinctTheaterIds).isEqualTo(Collections.emptyList());
        }

        @Test
        @DisplayName("영화관 목록 조회 성공")
        public void shouldGetTheatersByIds() throws Exception {
            // given
            Long movieId = 1L;
            Long movieId2 = 4L;
            saveDummyTheaterSchedules();

            // when
            List<Long> distinctTheaterIds = theaterQueryService.findDistinctTheaterIds(new FindDistinctTheaterIdsByMovieIdQuery(movieId));

            // then
            assertThat(distinctTheaterIds).hasSize(2);
            assertThat(distinctTheaterIds).contains(101L, 103L);

            // given 2
            Long theaterId = 101L;

            // when 2
            List<Long> distinctTheaterIds2 = theaterQueryService.findDistinctTheaterIds(new FindDistinctTheaterIdsByMovieIdQuery(movieId2));

            // then 2
            assertThat(distinctTheaterIds2).hasSize(1);
        }
    }

    @Nested
    @DisplayName("영화관 조회 테스트")
    class GetTheaterTest {
        @Test
        @DisplayName("영화관 조회 실패 - 영화관 데이터가 존재하지 않는 경우")
        public void shouldThrowExceptionWhenTheaterIsNotFound() throws Exception {
            // given
            Long theaterId = 99L;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterQueryService.getTheater(new GetTheaterByIdQuery(theaterId)));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_NOT_FOUND);
        }

        @Test
        @DisplayName("영화관 조회 성공")
        public void shouldSuccessfullyGetTheater() throws Exception {
            // given
            List<Theater> theaters = saveDummyTheaters();
            System.out.println("Dummy Theaters: " + theaters); // 👈 저장된 극장 정보 출력
            List<Long> theaterIds = theaters.stream()
                    .map(Theater::getId)
                    .toList();

            Long theaterId = theaterIds.get(0);

            System.out.println("Theater ID: " + theaterId);

            // when
            Theater theater = theaterQueryService.getTheater(new GetTheaterByIdQuery(theaterId));

            // then
            assertThat(theater.getName()).isEqualTo("CGV 강남");
            assertThat(theater.getAddress()).isEqualTo("서울특별시 강남구 강남대로 102길 23");
            assertThat(theater.getX()).isEqualTo("37.498095");
            assertThat(theater.getY()).isEqualTo("127.027610");
            assertThat(theater.getImg()).isEqualTo("/images/theaters/cgv-gangnam.png");
        }
    }

    @Nested
    @DisplayName("FindReservableTheaterSchedulesTest")
    class findReservableTheaterSchedulesTest {
        @Test
        @DisplayName("예매 가능한 영화 스케쥴 조회 성공 - 스케쥴이 존재하지 않는 경우")
        public void shouldSuccessFindReservableTheaterSchedulesWhenTheaterScheduleNotExist() throws Exception {
            // given
            Long theaterId = 1L;
            Long movieId = 1L;

            // when
            List<TheaterSchedule> reservableTheaterSchedules = theaterQueryService.findReservableTheaterSchedules(new FindReservableTheaterSchedulesQuery(theaterId, movieId));

            // then
            assertThat(reservableTheaterSchedules).hasSize(0);
        }

        @Test
        @DisplayName("예매 가능한 영화 영화 스케쥴 조회 성공")
        @Rollback(value = false)
        public void shouldSuccessFindReservableTheaterSchedules() throws Exception {
            // given
            List<TheaterSchedule> theaterSchedules = saveDummyTheaterSchedules();
            TheaterSchedule theaterSchedule = theaterSchedules.get(0);
            Long theaterId = theaterSchedule.getTheaterId();
            Long movieId = theaterSchedule.getMovieId();

            // when
            List<TheaterSchedule> reservableTheaterSchedules = theaterQueryService.findReservableTheaterSchedules(new FindReservableTheaterSchedulesQuery(theaterId, movieId));

            // then
            assertThat(reservableTheaterSchedules).isNotEmpty();
            assertThat(reservableTheaterSchedules)
                    .allSatisfy(schedule -> {
                        assertThat(schedule.getTheaterId()).isEqualTo(theaterId);
                        assertThat(schedule.getMovieId()).isEqualTo(movieId);
                    });
            assertThat(reservableTheaterSchedules).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findReservableTheaterSeats 통합 테스트")
    class findReservableTheaterSeatsTest {
        @Test
        @DisplayName("예약 가능한 영화관 좌석 조회 성공 - 좌석 없음")
        public void shouldSuccessFindReservableTheaterSeatsWhenNotExistData() throws Exception {
            // given
            Long theaterScheduleId = 1L;

            // when
            List<TheaterSeat> reservableTheaterSeats = theaterQueryService.findReservableTheaterSeats(new TheaterQuery.FindReservableTheaterSeatsQuery(theaterScheduleId, false));

            // then
            assertThat(reservableTheaterSeats).hasSize(0);
        }

        @Test
        @DisplayName("예약 가능한 영화관 좌석 조회 성공")
        public void shouldSuccessFindReservableTheaterSeats() {
            // Given
            List<TheaterSchedule> schedules = saveDummyTheaterSchedules();
            List<TheaterSeat> seats = saveDummyTheaterSeats(schedules);

            Long theaterScheduleId = schedules.get(0).getId(); // 첫 번째 스케줄 ID 가져오기

            // When
            List<TheaterSeat> reservableSeats = theaterQueryService.findReservableTheaterSeats(
                    new TheaterQuery.FindReservableTheaterSeatsQuery(theaterScheduleId, false)
            );

            // Then
            assertThat(reservableSeats).isNotEmpty(); // 예약 가능한 좌석이 있어야 함
            assertThat(reservableSeats).allMatch(seat -> !seat.getIsReserved()); // 모든 좌석이 예약되지 않은 상태여야 함

            // 전체 좌석 중 예약되지 않은 좌석 개수와 비교
            long totalAvailableSeats = seats.stream()
                    .filter(seat -> seat.getTheaterScheduleId().equals(theaterScheduleId) && !seat.getIsReserved())
                    .count();

            assertThat(reservableSeats).hasSize((int) totalAvailableSeats);
        }
    }

    // 더미 데이터 저장
    public List<Long> saveDummyMovies() {
        List<Movie> movies = movieJpaRepository.saveAll(List.of(
                Movie.builder()
                        .title("영화 A")
                        .screeningStartDate(LocalDate.now())
                        .screeningEndDate(LocalDate.now().plusDays(3))
                        .build(),

                Movie.builder()
                        .title("영화 B")
                        .screeningStartDate(LocalDate.now().plusDays(1))
                        .screeningEndDate(LocalDate.now().plusDays(5))
                        .build(),

                Movie.builder()
                        .title("영화 C")
                        .screeningStartDate(LocalDate.now().plusDays(2))
                        .screeningEndDate(LocalDate.now().plusDays(6))
                        .build(),

                Movie.builder()
                        .title("영화 D")
                        .screeningStartDate(LocalDate.now().plusDays(3))
                        .screeningEndDate(LocalDate.now().plusDays(7))
                        .build()
        ));

        return movies.stream().map(Movie::getId).collect(Collectors.toList());
    }

    // 더미 데이터 입력
    public List<TheaterSchedule> saveDummyTheaterSchedules() {
        List<TheaterSchedule> theaterSchedules = theaterScheduleJpaRepository.saveAll(List.of(
                TheaterSchedule.builder()
                        .movieId(1L)
                        .theaterId(101L)
                        .theaterScreenId(201L)
                        .movieAt(LocalDateTime.now().plusDays(1))
                        .reservationStartAt(LocalDateTime.now().minusHours(2))
                        .reservationEndAt(LocalDateTime.now().plusHours(5))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(1L)
                        .theaterId(101L)
                        .theaterScreenId(202L)
                        .movieAt(LocalDateTime.now().plusDays(2))
                        .reservationStartAt(LocalDateTime.now().minusHours(3))
                        .reservationEndAt(LocalDateTime.now().plusHours(6))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(1L)
                        .theaterId(103L)
                        .theaterScreenId(203L)
                        .movieAt(LocalDateTime.now().plusDays(3).withHour(14).withMinute(0)) // 오후 2시
                        .reservationStartAt(LocalDateTime.now().minusHours(4))
                        .reservationEndAt(LocalDateTime.now().plusHours(7))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(4L)
                        .theaterId(105L)
                        .theaterScreenId(204L)
                        .movieAt(LocalDateTime.now().plusDays(4).withHour(18).withMinute(30)) // 오후 6시 30분
                        .reservationStartAt(LocalDateTime.now().plusHours(5))
                        .reservationEndAt(LocalDateTime.now().plusHours(8))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(4L)
                        .theaterId(105L)
                        .theaterScreenId(205L)
                        .movieAt(LocalDateTime.now().plusDays(5).withHour(20).withMinute(0)) // 오후 8시
                        .reservationStartAt(LocalDateTime.now().plusHours(6))
                        .reservationEndAt(LocalDateTime.now().plusHours(9))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(6L)
                        .theaterId(106L)
                        .theaterScreenId(206L)
                        .movieAt(LocalDateTime.now().plusDays(6).withHour(16).withMinute(45)) // 오후 4시 45분
                        .reservationStartAt(LocalDateTime.now().plusHours(7))
                        .reservationEndAt(LocalDateTime.now().plusHours(10))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(7L)
                        .theaterId(107L)
                        .theaterScreenId(207L)
                        .movieAt(LocalDateTime.now().plusDays(7).withHour(10).withMinute(15)) // 오전 10시 15분
                        .reservationStartAt(LocalDateTime.now().plusHours(8))
                        .reservationEndAt(LocalDateTime.now().plusHours(11))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(8L)
                        .theaterId(108L)
                        .theaterScreenId(208L)
                        .movieAt(LocalDateTime.now().plusDays(8).withHour(12).withMinute(0)) // 정오 12시
                        .reservationStartAt(LocalDateTime.now().plusHours(9))
                        .reservationEndAt(LocalDateTime.now().plusHours(12))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ));

        return theaterSchedules;
    }

    // 더미 데이터 입력
    public List<Theater> saveDummyTheaters() {
        List<Theater> theaters = theaterJpaRepository.saveAll(List.of(
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
                        .name("롯데시네마 월드타워")
                        .address("서울특별시 송파구 올림픽로 300 롯데월드몰 5층")
                        .img("/images/theaters/lotte-worldtower.png")
                        .x("37.513272")
                        .y("127.104872")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Theater.builder()
                        .name("메가박스 코엑스")
                        .address("서울특별시 강남구 삼성동 159 코엑스몰")
                        .img("/images/theaters/megabox-coex.png")
                        .x("37.511293")
                        .y("127.059027")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Theater.builder()
                        .name("CGV 용산아이파크몰")
                        .address("서울특별시 용산구 한강대로23길 55 아이파크몰 6층")
                        .img("/images/theaters/cgv-yongsan.png")
                        .x("37.529849")
                        .y("126.964642")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Theater.builder()
                        .name("롯데시네마 건대입구")
                        .address("서울특별시 광진구 능동로 92")
                        .img("/images/theaters/lotte-kondae.png")
                        .x("37.540459")
                        .y("127.069128")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Theater.builder()
                        .name("메가박스 신촌")
                        .address("서울특별시 서대문구 신촌로 129")
                        .img("/images/theaters/megabox-shinchon.png")
                        .x("37.555260")
                        .y("126.936838")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Theater.builder()
                        .name("CGV 홍대")
                        .address("서울특별시 마포구 양화로 104")
                        .img("/images/theaters/cgv-hongdae.png")
                        .x("37.557340")
                        .y("126.924608")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Theater.builder()
                        .name("메가박스 강남")
                        .address("서울특별시 서초구 강남대로 419")
                        .img("/images/theaters/megabox-gangnam.png")
                        .x("37.501277")
                        .y("127.025046")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ));

        return theaters;
    }

    public List<TheaterSeat> saveDummyTheaterSeats(List<TheaterSchedule> theaterSchedules) {
        List<TheaterSeat> seats = new ArrayList<>();

        for (TheaterSchedule schedule : theaterSchedules) {
            for (int i = 1; i <= 10; i++) { // 좌석 10개 생성
                TheaterSeat seat = TheaterSeat.builder()
                        .theaterScheduleId(schedule.getId())
                        .number(i)
                        .price(10000 + (i * 500)) // 좌석 번호에 따라 가격 변동
                        .isReserved(false) // 기본적으로 예약되지 않음
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                seats.add(seat);
            }
        }
        return theaterSeatJpaRepository.saveAll(seats);
    }

}