package com.tdd.movie.interfaces.api.controller;

import com.tdd.movie.interfaces.api.dto.ReservationControllerDto.GetReservationResponse;
import com.tdd.movie.interfaces.api.dto.ReservationControllerDto.PayReservationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Reservation", description = "영화관 예매 내역 API")
public interface IReservationController {

    @Operation(summary = "영화관 예매 내역 결재", description = "영화관의 예매 내역을 결재 합니다.")
    ResponseEntity<PayReservationResponse> payReservation(
            @Schema(description = "영화관 예매 내역 ID", example = "1")
            Long reservationId,
            @Schema(description = "사용자 ID", example = "1")
            Long userId
    );

    @Operation(summary = "영화관 예매 내역 조회", description = "영화관 예매 내역을 조회 합니다.")
    ResponseEntity<GetReservationResponse> getReservation(
            @Schema(description = "영화관 예매 내역 ID", example = "1")
            Long reservationId,
            @Schema(description = "사용자 ID", example = "1")
            Long userId
    );

}
