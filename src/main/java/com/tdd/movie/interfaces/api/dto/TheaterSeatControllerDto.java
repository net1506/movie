package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.theater.domain.Reservation;
import com.tdd.movie.domain.theater.domain.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class TheaterSeatControllerDto {
    public record ReserveSeatResponse(
            ReservationResponse reservation
    ) {

    }

    public record ReservationResponse(
            @Schema(description = "예매 내역 ID", example = "1")
            Long id,

            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "예매 내역 상태", example = "WAITING")
            ReservationStatus status,

            @Schema(description = "예매 날짜", example = "2025-02-05T00:00:00")
            LocalDateTime reservedAt
    ) {
        public ReservationResponse(Reservation reservation) {
            this(
                    reservation.getId(),
                    reservation.getUserId(),
                    reservation.getStatus(),
                    reservation.getReservedAt()
            );
        }
    }
}
