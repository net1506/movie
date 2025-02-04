package com.tdd.movie.interfaces.api.controller;

import com.tdd.movie.interfaces.api.dto.TheaterControllerDto.GetAvailableSchedulesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface ITheaterScheduleController {

    @Operation(summary = "예약 가능한 영화관 좌석 조회", description = "예약 가능한 영화관 좌석을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 가능한 영화관 좌석 조회 성공")
    ResponseEntity<GetAvailableSchedulesResponse> getAvailableSeats(
            @Schema(description = "영화관 스케쥴 ID", example = "1")
            @PathVariable Long theaterScheduleId
    );

}
