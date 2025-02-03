package com.tdd.movie.domain.movie.service;

import com.tdd.movie.domain.movie.dto.MovieQuery.FindPlayingMoviesByDatePeriodQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.FindUpcomingMoviesByDateAfterQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.GetMovieByIdQuery;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.infra.db.movie.MovieJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("MovieQueryService 단위 테스트")
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
    @DisplayName("GetMovie 테스트")
    class GetMovieTest {

        @Test
        @DisplayName("조회 실패 - 영화 ID 가 존재하지 않는 경우")
        public void shouldThrowExceptionWhenMovieIsNotFound() throws Exception {
            // given
            Long movieId = 1L;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> movieQueryService.getMovie(new GetMovieByIdQuery(movieId)));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(MOVIE_NOT_FOUND);
            assertThat(coreException.getMessage()).isEqualTo(MOVIE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("조회 성공")
        public void shouldGetMovie() throws Exception {
            // given
            List<Long> savedIds = saveDummyMovies();
            List<String> expectedTitles = List.of("영화 A", "영화 B", "영화 C", "영화 D");

            // when
            // then
            for (int i = 0; i < savedIds.size(); i++) {
                Long savedId = savedIds.get(i);
                Movie movie = movieQueryService.getMovie(new GetMovieByIdQuery(savedId));

                // 검증: 저장된 순서와 동일한 타이틀을 가져오는지 확인
                assertThat(movie.getTitle()).isEqualTo(expectedTitles.get(i));
            }
        }

    }

    @Nested
    @DisplayName("GetMoviesByDatePeriodQuery 테스트")
    class FindPlayingMoviesByDatePeriodQueryTest {

        @Test
        @DisplayName("조회 실패 - screeningDate 가 NULL 인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsNull() {
            // given
            LocalDate screeningDate = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    movieQueryService.findPlayingMovies(new FindPlayingMoviesByDatePeriodQuery(screeningDate)));

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
            List<Movie> movies = movieQueryService.findPlayingMovies(new FindPlayingMoviesByDatePeriodQuery(screeningDate));

            // then
            assertThat(movies).hasSize(1);
            assertThat(movies).extracting(Movie::getTitle).contains("영화 A");
        }
    }

    @Nested
    @DisplayName("GetMoviesByDateAfterQuery 테스트")
    class FindUpcomingMoviesByDateAfterQueryTest {

        @Test
        @DisplayName("조회 실패 - screeningDate 가 NULL 인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsNull() {
            // given
            LocalDate screeningDate = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    movieQueryService.findUpcomingMovies(new FindUpcomingMoviesByDateAfterQuery(screeningDate)));

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
                    movieQueryService.findUpcomingMovies(new FindUpcomingMoviesByDateAfterQuery(screeningDate)));

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
            List<Movie> movies = movieQueryService.findUpcomingMovies(new FindUpcomingMoviesByDateAfterQuery(screeningDate));

            // then
            assertThat(movies).hasSize(3);
            assertThat(movies).extracting(Movie::getTitle).contains("영화 B", "영화 C", "영화 D");
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
}