package com.tdd.movie.domain.theater.dto;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.theater.dto.TheaterCommand.CreateReservationCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_SEAT_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.User.USER_ID_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TheaterCommand 단위 테스트")
class TheaterCommandTest {

    @Nested
    @DisplayName(" 단위 테스트")
    class CreateReservationCommandTest {
        @Test
        @DisplayName("영화 예매 내역 저장 생성자 생성 실패 - theaterSeatId 가 NULL 인 경우")
        public void shouldThrowExceptionWhenTheaterSeatIdIsNull() throws Exception {
            // given
            Long theaterSeatId = null;
            Long userId = 1L;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new CreateReservationCommand(theaterSeatId, userId));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(THEATER_SEAT_ID_MUST_NOT_BE_NULL);
            assertThat(coreException.getMessage()).isEqualTo(THEATER_SEAT_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 저장 생성자 생성 실패 - userId 가 NULL 인 경우")
        public void shouldThrowExceptionWhenUserIdIsNull() throws Exception {
            // given
            Long theaterSeatId = 1L;
            Long userId = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new CreateReservationCommand(theaterSeatId, userId));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(USER_ID_MUST_NOT_BE_NULL);
            assertThat(coreException.getMessage()).isEqualTo(USER_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("영화 예매 내역 저장 생성자 생성 성공")
        public void shouldSuccessCreateReservationCommand() throws Exception {
            // given
            Long theaterSeatId = 1L;
            Long userId = 2L;

            // when
            CreateReservationCommand createReservationCommand = new CreateReservationCommand(theaterSeatId, userId);

            // then
            assertThat(createReservationCommand.theaterSeatId()).isEqualTo(theaterSeatId);
            assertThat(createReservationCommand.userId()).isEqualTo(userId);
        }
    }
}