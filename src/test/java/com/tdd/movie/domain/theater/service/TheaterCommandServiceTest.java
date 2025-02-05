package com.tdd.movie.domain.theater.service;

import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.dto.TheaterCommand.CreateReservationCommand;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetReservationByIdQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("TheaterCommandService 단위 테스트")
class TheaterCommandServiceTest {

    @Autowired
    private TheaterCommandService theaterCommandService;

    @Autowired
    private TheaterQueryService theaterQueryService;

    @Test
    @DisplayName("영화관 예매 내역 저장 성공")
    public void shouldSuccessCreateReservation() throws Exception {
        // given
        Long theaterSeatId = 1L;
        Long userId = 1L;
        Reservation reservation = theaterCommandService.createReservation(new CreateReservationCommand(theaterSeatId, userId));

        // when
        Reservation fetchedReservation = theaterQueryService.getReservation(new GetReservationByIdQuery(reservation.getId()));

        // then
        assertThat(fetchedReservation).isNotNull();
        assertThat(fetchedReservation.getUserId()).isEqualTo(userId);
        assertThat(fetchedReservation.getTheaterSeatId()).isEqualTo(theaterSeatId);
    }
}