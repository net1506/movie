package com.tdd.movie.domain.theater.dto;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.dto.TheaterQuery.FindReservableTheaterSchedulesQuery;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetTheaterByIdQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tdd.movie.domain.support.error.ErrorType.Movie.MOVIE_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_EMPTY;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_ID_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("TheaterQuery 단위 테스트")
class TheaterQueryTest {

    @Nested
    @DisplayName("GetTheatersByIds 생성자 테스트")
    class FindTheatersByIdsQueryParamTest {
        @Test
        @DisplayName("생성자 생성 실패 - theaterIds 가 NULL 인 경우")
        public void shouldThrowExceptionWhenTheaterIdsIsNull() {
            // given
            List<Long> theaterIds = null;

            // when
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new TheaterQuery.FindTheatersByIdsQuery(theaterIds));

            // then
            assertThat(coreException.getMessage()).isEqualTo(THEATER_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 실패 - theaterIds 가 빈 리스트인 경우")
        public void shouldThrowExceptionWhenTheaterIdsIsEmpty() {
            // given
            List<Long> theaterIds = List.of();

            // when
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new TheaterQuery.FindTheatersByIdsQuery(theaterIds));

            // then
            assertThat(coreException.getMessage()).isEqualTo(THEATER_ID_MUST_NOT_BE_EMPTY.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldCreateGetTheatersByIds() {
            // given
            List<Long> theaterIds = List.of(1L, 2L, 3L);

            // when
            TheaterQuery.FindTheatersByIdsQuery query = new TheaterQuery.FindTheatersByIdsQuery(theaterIds);

            // then
            assertThat(query.theaterIds()).isEqualTo(theaterIds);
        }
    }

    @Nested
    @DisplayName("GetDistinctTheaterIdsByMovieId 생성자 테스트")
    class FindDistinctTheaterIdsByMovieIdQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - movieId 가 NULL 인 경우")
        public void shouldThrowExceptionWhenMovieIdIsNull() {
            // given
            Long movieId = null;

            // when
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new TheaterQuery.FindDistinctTheaterIdsByMovieIdQuery(movieId));

            // then
            assertThat(coreException.getMessage()).isEqualTo(MOVIE_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldCreateGetDistinctTheaterIdsByMovieId() {
            // given
            Long movieId = 1L;

            // when
            TheaterQuery.FindDistinctTheaterIdsByMovieIdQuery query = new TheaterQuery.FindDistinctTheaterIdsByMovieIdQuery(movieId);

            // then
            assertThat(query.movieId()).isEqualTo(movieId);
        }
    }

    @Nested
    @DisplayName("GetTheaterByIdQuery 생성자 테스트")
    class GetTheaterByIdQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - theaterId 가 NULL 인 경우")
        public void shouldThrowExceptionWhenTheaterIdIsNull() {
            // given
            Long theaterId = null;

            // when
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new GetTheaterByIdQuery(theaterId));

            // then
            assertThat(coreException.getMessage()).isEqualTo(THEATER_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldCreateGetTheaterByIdQuery() {
            // given
            Long theaterId = 1L;

            // when
            GetTheaterByIdQuery query = new GetTheaterByIdQuery(theaterId);

            // then
            assertThat(query.theaterId()).isEqualTo(theaterId);
        }
    }

    @Nested
    @DisplayName("FindReservableTheaterSchedulesQuery 생성자 테스트")
    class FindReservableTheaterSchedulesQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - theaterId 가 NULL 인 경우")
        public void shouldThrowExceptionWhenTheaterIdIsNull() {
            // given
            Long theaterId = null;
            Long movieId = 1L;

            // when
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new FindReservableTheaterSchedulesQuery(theaterId, movieId));

            // then
            assertThat(coreException.getMessage()).isEqualTo(THEATER_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 실패 - movieId 가 NULL 인 경우")
        public void shouldThrowExceptionWhenMovieIdIsNull() {
            // given
            Long theaterId = 1L;
            Long movieId = null;

            // when
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new FindReservableTheaterSchedulesQuery(theaterId, movieId));

            // then
            assertThat(coreException.getMessage()).isEqualTo(MOVIE_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldCreateFindReservableTheaterSchedulesQuery() {
            // given
            Long theaterId = 1L;
            Long movieId = 2L;

            // when
            FindReservableTheaterSchedulesQuery query = new FindReservableTheaterSchedulesQuery(theaterId, movieId);

            // then
            assertThat(query.theaterId()).isEqualTo(theaterId);
            assertThat(query.movieId()).isEqualTo(movieId);
        }
    }
}