package com.tdd.movie.domain.movie.dto;

import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDateAfterQuery;
import com.tdd.movie.domain.movie.dto.MovieQuery.GetMoviesByDatePeriodQuery;
import com.tdd.movie.domain.support.error.CoreException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.INVALID_SCREENING_DATE;
import static com.tdd.movie.domain.support.error.ErrorType.Movie.SCREENING_DATE_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MovieQueryTest 단위 테스트")
class MovieQueryTest {

    @Nested
    @DisplayName("GetMoviesByDatePeriodQuery 생성자 테스트")
    class GetMoviesByDatePeriodQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - screeningDate 가 NULL 인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsNull() {
            // given
            LocalDate screeningDate = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new GetMoviesByDatePeriodQuery(screeningDate));

            // then
            assertThat(coreException.getMessage()).isEqualTo(SCREENING_DATE_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldCreateGetMoviesByDatePeriodQuery() {
            // given
            LocalDate screeningDate = LocalDate.now();

            // when
            GetMoviesByDatePeriodQuery query = new GetMoviesByDatePeriodQuery(screeningDate);

            // then
            assertThat(query.screeningDate()).isEqualTo(screeningDate);
        }
    }

    @Nested
    @DisplayName("GetMoviesByDateAfterQuery 생성자 테스트")
    class GetMoviesByDateAfterQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - screeningDate 가 NULL 인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsNull() {
            // given
            LocalDate screeningDate = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new GetMoviesByDateAfterQuery(screeningDate));

            // then
            assertThat(coreException.getMessage()).isEqualTo(SCREENING_DATE_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 실패 - screeningDate 가 과거인 경우")
        public void shouldThrowExceptionWhenScreeningDateIsPast() {
            // given
            LocalDate screeningDate = LocalDate.now().minusDays(1);

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new GetMoviesByDateAfterQuery(screeningDate));

            // then
            assertThat(coreException.getMessage()).isEqualTo(INVALID_SCREENING_DATE.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldCreateGetMoviesByDateAfterQuery() {
            // given
            LocalDate screeningDate = LocalDate.now().plusDays(1);

            // when
            GetMoviesByDateAfterQuery query = new GetMoviesByDateAfterQuery(screeningDate);

            // then
            assertThat(query.screeningDate()).isEqualTo(screeningDate);
        }
    }

}