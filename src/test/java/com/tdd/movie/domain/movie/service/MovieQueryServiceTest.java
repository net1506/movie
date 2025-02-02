package com.tdd.movie.domain.movie.service;

import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDateAfterQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDatePeriodQuery;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.INVALID_SCREENING_DATE;
import static com.tdd.movie.domain.support.error.ErrorType.Movie.SCREENING_DATE_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("MovieQueryService 단윝 테스트")
class MovieQueryServiceTest {

    @Autowired
    private MovieQueryService movieQueryService;

    @Autowired
    private MovieJpaRepository movieJpaRepository;

    @BeforeEach
    public void setUp() {
        movieJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("GetMoviesByDatePeriodQuery 테스트")
    class GetMoviesByDatePeriodQueryTest {

        @Test
        @DisplayName("조회 실패 - screeningDate 가 NULL 인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsNull() {
            // given
            LocalDate screeningDate = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    movieQueryService.getMovies(new GetMoviesByDatePeriodQuery(screeningDate)));

            // then
            assertThat(coreException.getMessage()).isEqualTo(SCREENING_DATE_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("조회 성공 - 특정 날짜에 상영 중인 영화 조회")
        public void shouldGetMoviesByDatePeriod() {
            // given
            LocalDate screeningDate = LocalDate.now();
            saveDummyMovies();

            // when
            List<Movie> movies = movieQueryService.getMovies(new GetMoviesByDatePeriodQuery(screeningDate));

            // then
            assertThat(movies).hasSize(1);
            assertThat(movies).extracting(Movie::getTitle).contains("영화 A");
        }
    }

    @Nested
    @DisplayName("GetMoviesByDateAfterQuery 테스트")
    class GetMoviesByDateAfterQueryTest {

        @Test
        @DisplayName("조회 실패 - screeningDate 가 NULL 인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsNull() {
            // given
            LocalDate screeningDate = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    movieQueryService.getMovies(new GetMoviesByDateAfterQuery(screeningDate)));

            // then
            assertThat(coreException.getMessage()).isEqualTo(SCREENING_DATE_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("조회 실패 - screeningDate 가 과거인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsPast() {
            // given
            LocalDate screeningDate = LocalDate.now().minusDays(1);

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    movieQueryService.getMovies(new GetMoviesByDateAfterQuery(screeningDate)));

            // then
            assertThat(coreException.getMessage()).isEqualTo(INVALID_SCREENING_DATE.getMessage());
        }

        @Test
        @DisplayName("조회 성공 - 특정 날짜 이후 상영 예정 영화 조회")
        public void shouldGetMoviesByDateAfter() {
            // given
            LocalDate screeningDate = LocalDate.now();
            saveDummyMovies();

            // when
            List<Movie> movies = movieQueryService.getMovies(new GetMoviesByDateAfterQuery(screeningDate));

            // then
            assertThat(movies).hasSize(3);
            assertThat(movies).extracting(Movie::getTitle).contains("영화 B", "영화 C", "영화 D");
        }
    }

    // 더미 데이터 저장
    public void saveDummyMovies() {
        movieJpaRepository.saveAll(List.of(
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
    }
}