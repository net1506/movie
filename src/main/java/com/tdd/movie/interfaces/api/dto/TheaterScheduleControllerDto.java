package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.theater.model.TheaterSeat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class TheaterScheduleControllerDto {

    public record GetAvailableSeatsResponse(
            List<TheaterSeatResponse> theaterSchedules
    ) {

    }

    public record TheaterSeatResponse(
            @Schema(description = "상영 좌석 ID", example = "10")
            Long id,

            @Schema(description = "상영 일정 ID", example = "1")
            Long theaterScheduleId,

            @Schema(description = "좌석 번호", example = "2")
            Integer number,

            @Schema(description = "좌석 가격", example = "15000")
            Integer price,

            @Schema(description = "예약 여부", example = "true")
            Boolean isReserved
    ) {
        public TheaterSeatResponse(TheaterSeat theaterSeat) {
            this(
                    theaterSeat.getId(),
                    theaterSeat.getTheaterScheduleId(),
                    theaterSeat.getNumber(),
                    theaterSeat.getPrice(),
                    theaterSeat.getIsReserved()
            );
        }
    }
}
