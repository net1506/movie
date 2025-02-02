package com.tdd.movie.application;


import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterJpaRepository;
import com.tdd.movie.infra.db.theater.TheaterScheduleJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("MovieFacade 통합 테스트")
class MovieFacadeTest {

    @Autowired
    private MovieJpaRepository movieJpaRepository;

    @Autowired
    private TheaterJpaRepository theaterJpaRepository;

    @Autowired
    private TheaterScheduleJpaRepository theaterScheduleJpaRepository;

    @Autowired
    private MovieFacade movieFacade;

    @Nested
    @DisplayName("getPlayingMovies 메서드 테스트")
    class GetPlayingMoviesTest {

        @Test
        @DisplayName("영화가 없을 때 빈 리스트를 반환해야 한다")
        void shouldReturnEmptyList_WhenNoMoviesExist() {
            // given
            movieJpaRepository.deleteAll();

            // when
            List<Movie> movies = movieFacade.getPlayingMovies(LocalDate.now());

            // then
            assertThat(movies).isEmpty();
        }

        @Test
        @DisplayName("조건에 맞는 영화만 조회해야 한다")
        void shouldReturnMovies_WhenMatchingMoviesExist() {
            // given
            LocalDate today = LocalDate.now();
            movieJpaRepository.save(Movie.builder()
                    .id(1L)
                    .title("영화1")
                    .plot("꿈과 현실을 넘나드는 이야기")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().minusDays(3))
                    .screeningEndDate(LocalDate.now().plusMonths(1))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            // when
            List<Movie> movies = movieFacade.getPlayingMovies(today);

            // then
            assertThat(movies).hasSize(1).extracting(Movie::getTitle).contains("영화1");
        }

        @Test
        @DisplayName("조건에 맞지 않는 영화만 존재할 경우 빈 리스트를 반환해야 한다")
        void shouldReturnEmptyList_WhenOnlyNonMatchingMoviesExist() {
            // given
            LocalDate futureDate = LocalDate.now().plusDays(10);
            movieJpaRepository.save(Movie.builder()
                    .id(1L)
                    .title("영화1")
                    .plot("꿈과 현실을 넘나드는 이야기")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().plusDays(3))
                    .screeningEndDate(LocalDate.now().plusMonths(1))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            // when
            List<Movie> movies = movieFacade.getPlayingMovies(LocalDate.now());

            // then
            assertThat(movies).isEmpty();
        }

        @Test
        @DisplayName("조건에 맞는 영화와 맞지 않는 영화가 섞여 있을 경우 맞는 영화만 반환해야 한다")
        void shouldReturnOnlyMatchingMovies_WhenBothExist() {
            // given
            LocalDate today = LocalDate.now();
            movieJpaRepository.save(Movie.builder()
                    .id(1L)
                    .title("영화1")
                    .plot("꿈과 현실을 넘나드는 이야기1")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().minusDays(3))
                    .screeningEndDate(LocalDate.now().plusMonths(1))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()
            );
            movieJpaRepository.save(Movie.builder()
                    .id(2L)
                    .title("영화2")
                    .plot("꿈과 현실을 넘나드는 이야기2")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().plusDays(3))
                    .screeningEndDate(LocalDate.now().plusMonths(1))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()
            );

            // when
            List<Movie> movies = movieFacade.getPlayingMovies(today);

            // then
            assertThat(movies).hasSize(1).extracting(Movie::getTitle).contains("영화1");
        }
    }

    @Nested
    @DisplayName("getUpcomingMovies 메서드 테스트")
    class GetUpcomingMoviesTest {
        @Test
        @DisplayName("예정된 영화가 없을 때 빈 리스트를 반환해야 한다")
        void shouldReturnEmptyList_WhenNoUpcomingMoviesExist() {
            // given
            movieJpaRepository.deleteAll();

            // when
            List<Movie> movies = movieFacade.getUpcomingMovies(LocalDate.now());

            // then
            assertThat(movies).isEmpty();
        }


        @Test
        @DisplayName("예정된 영화가 존재하면 조회해야 한다")
        void shouldReturnUpcomingMovies_WhenMatchingMoviesExist() {
            // given
            LocalDate futureDate = LocalDate.now().plusDays(5);
            movieJpaRepository.save(Movie.builder()
                    .id(1L)
                    .title("영화1")
                    .plot("꿈과 현실을 넘나드는 이야기")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().plusWeeks(2))
                    .screeningEndDate(LocalDate.now().plusMonths(1))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            movieJpaRepository.save(Movie.builder()
                    .id(2L)
                    .title("영화2")
                    .plot("꿈과 현실을 넘나드는 이야기2")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().plusWeeks(3))
                    .screeningEndDate(LocalDate.now().plusMonths(2))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()
            );
            // when
            List<Movie> movies = movieFacade.getUpcomingMovies(LocalDate.now());

            // then
            assertThat(movies).hasSize(2).extracting(Movie::getTitle).contains("영화1", "영화2");
        }
    }

    @Nested
    @DisplayName("getScreeningTheaters 메서드 테스트")
    class GetScreeningTheatersTest {

        @Test
        @DisplayName("해당 영화의 상영관이 없을 때 빈 리스트를 반환해야 한다")
        void shouldReturnEmptyList_WhenNoTheatersExist() {
            // given
            movieJpaRepository.deleteAll();
            movieJpaRepository.save(Movie.builder()
                    .id(1L)
                    .title("영화1")
                    .plot("꿈과 현실을 넘나드는 이야기")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().plusWeeks(3))
                    .screeningEndDate(LocalDate.now().plusMonths(2))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            // when
            List<Theater> theaters = movieFacade.getScreeningTheaters(2L);

            // then
            assertThat(theaters).isEmpty();
        }

        @Test
        @DisplayName("해당 영화의 상영관이 존재하면 조회해야 한다")
        void shouldReturnTheaters_WhenMatchingTheatersExist() {
            // given
            Movie movie = movieJpaRepository.save(Movie.builder()
                    .id(1L)
                    .title("영화1")
                    .plot("꿈과 현실을 넘나드는 이야기")
                    .posterImageUrl("https://example.com/inception.jpg")
                    .runningTime(148)
                    .screeningStartDate(LocalDate.now().plusWeeks(3))
                    .screeningEndDate(LocalDate.now().plusMonths(2))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
            Theater theater1 = theaterJpaRepository.save(Theater.builder().name("신도림관").address("신도림관 주소").build());
            Theater theater2 = theaterJpaRepository.save(Theater.builder().name("영등포관").address("영등포관 주소").build());

            List<TheaterSchedule> theaterSchedules = List.of(TheaterSchedule.builder()
                            .movieId(movie.getId())
                            .theaterId(theater1.getId())
                            .theaterScreenId(2L)
                            .movieAt(LocalDateTime.of(2025, 3, 10, 18, 30))
                            .reservationStartAt(LocalDateTime.of(2025, 2, 20, 9, 0))
                            .reservationEndAt(LocalDateTime.of(2025, 3, 9, 23, 59))
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    TheaterSchedule.builder()
                            .movieId(movie.getId())
                            .theaterId(theater2.getId())
                            .theaterScreenId(3L)
                            .movieAt(LocalDateTime.of(2025, 3, 10, 18, 30))
                            .reservationStartAt(LocalDateTime.of(2025, 2, 20, 9, 0))
                            .reservationEndAt(LocalDateTime.of(2025, 3, 9, 23, 59))
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());
            theaterScheduleJpaRepository.saveAll(theaterSchedules);

            // when
            List<Theater> theaters = movieFacade.getScreeningTheaters(movie.getId());

            // then
            assertThat(theaters).hasSize(2).extracting(Theater::getName).contains("신도림관", "영등포관");
        }
    }
}