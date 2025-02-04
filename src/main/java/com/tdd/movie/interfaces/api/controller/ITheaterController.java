package com.tdd.movie.interfaces.api.controller;

import com.tdd.movie.interfaces.api.dto.TheaterControllerDto.GetAvailableSchedulesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ITheaterController {

    @Operation(summary = "예매 가능한 영화관 일정 조회", description = "예매 가능한 영화관 일정을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예매 가능한 영화관 일정 조회 성공")
    ResponseEntity<GetAvailableSchedulesResponse> getAvailableSchedules(
            @Schema(description = "영화관 ID", example = "1")
            Long theaterId,

            @Schema(description = "영화 ID", example = "1")
            Long movieId
    );

}
