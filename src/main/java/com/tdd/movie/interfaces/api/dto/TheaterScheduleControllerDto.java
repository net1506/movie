package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class TheaterScheduleControllerDto {

    public record GetAvailableSeatsResponse(
            List<TheaterScheduleResponse> theaterSchedules
    ) {

    }

    public record TheaterScheduleResponse(
            @Schema(description = "상영 일정 ID", example = "1")
            Long id,

            @Schema(description = "영화 ID", example = "10")
            Long movieId,

            @Schema(description = "극장 ID", example = "5")
            Long theaterId,

            @Schema(description = "상영관 ID", example = "2")
            Long theaterScreenId,

            @Schema(description = "영화 상영 시간", example = "2025-02-10T14:30:00")
            LocalDateTime movieAt,

            @Schema(description = "예약 시작 시간", example = "2025-02-05T00:00:00")
            LocalDateTime reservationStartAt,

            @Schema(description = "예약 종료 시간", example = "2025-02-09T23:59:59")
            LocalDateTime reservationEndAt
    ) {
        public TheaterScheduleResponse(TheaterSchedule schedule) {
            this(
                    schedule.getId(),
                    schedule.getMovieId(),
                    schedule.getTheaterId(),
                    schedule.getTheaterScreenId(),
                    schedule.getMovieAt(),
                    schedule.getReservationStartAt(),
                    schedule.getReservationEndAt()
            );
        }
    }
}
