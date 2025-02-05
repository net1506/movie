package com.tdd.movie.interfaces.api.controller;

import com.tdd.movie.interfaces.api.dto.TheaterSeatControllerDto.ReserveSeatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ITheaterSeatController {

    @Operation(summary = "영화관 좌석 예매", description = "영화관 좌석을 예매 합니다.")
    @ApiResponse(responseCode = "200", description = "영화관 좌석 예매 성공")
    ResponseEntity<ReserveSeatResponse> reserveSeat(
            @Schema(description = "영화관 좌석 ID", example = "1")
            Long theaterSeatId,
            @Schema(description = "사용자 ID", example = "user1")
            Long userId
    );

}
