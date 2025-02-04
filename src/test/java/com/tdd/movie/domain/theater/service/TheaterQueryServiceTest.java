package com.tdd.movie.domain.theater.service;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindDistinctTheaterIdsByMovieIdQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindReservableTheaterSchedulesQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindTheatersByIdsQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetTheaterByIdQuery;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@DisplayName("TheaterQueryService 단위 테스트")
class TheaterQueryServiceTest {

    @Autowired
    TheaterQueryService theaterQueryService;

    @Autowired
    TheaterJpaRepository theaterJpaRepository;

    @Autowired
    TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @BeforeEach
    public void setUp() {
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
            Long theaterId = 1L;

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
            List<Long> theaterIds = theaters.stream()
                    .map(Theater::getId)
                    .toList();

            Long theaterId = theaterIds.get(0);

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


}