package com.tdd.movie.domain.theater.service;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindDistinctTheaterIdsByMovieId;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindTheatersByIds;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_EMPTY;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
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
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterQueryService.findTheaters(new FindTheatersByIds(threadIds)));

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
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> theaterQueryService.findTheaters(new FindTheatersByIds(threadIds)));

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
            List<Theater> theaters = theaterQueryService.findTheaters(new FindTheatersByIds(threadIds));

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
            List<Theater> theaters = theaterQueryService.findTheaters(new FindTheatersByIds(threadIds));

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
            List<Long> distinctTheaterIds = theaterQueryService.findDistinctTheaterIds(new FindDistinctTheaterIdsByMovieId(movieId));

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
            List<Long> distinctTheaterIds = theaterQueryService.findDistinctTheaterIds(new FindDistinctTheaterIdsByMovieId(movieId));

            // then
            assertThat(distinctTheaterIds).hasSize(2);
            assertThat(distinctTheaterIds).contains(101L, 103L);

            // given 2
            Long theaterId = 101L;

            // when 2
            List<Long> distinctTheaterIds2 = theaterQueryService.findDistinctTheaterIds(new FindDistinctTheaterIdsByMovieId(movieId2));

            // then 2
            assertThat(distinctTheaterIds2).hasSize(1);
        }
    }

    // 더미 데이터 입력
    public void saveDummyTheaterSchedules() {
        theaterScheduleJpaRepository.saveAll(List.of(
                TheaterSchedule.builder()
                        .movieId(1L)
                        .theaterId(101L)
                        .theaterScreenId(201L)
                        .movieAt(LocalDateTime.now().plusDays(1))
                        .reservationStartAt(LocalDateTime.now().plusHours(2))
                        .reservationEndAt(LocalDateTime.now().plusHours(5))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(1L)
                        .theaterId(101L)
                        .theaterScreenId(202L)
                        .movieAt(LocalDateTime.now().plusDays(2))
                        .reservationStartAt(LocalDateTime.now().plusHours(3))
                        .reservationEndAt(LocalDateTime.now().plusHours(6))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                TheaterSchedule.builder()
                        .movieId(1L)
                        .theaterId(103L)
                        .theaterScreenId(203L)
                        .movieAt(LocalDateTime.now().plusDays(3).withHour(14).withMinute(0)) // 오후 2시
                        .reservationStartAt(LocalDateTime.now().plusHours(4))
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
    }

}