package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.payment.model.Payment;
import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.model.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ReservationControllerDto {

    public record PayReservationResponse(
            PaymentResponse paymentResponse
    ) {

    }

    public record GetReservationResponse(
            ReservationResponse reservationResponse
    ) {

    }

    public record PaymentResponse(
            @Schema(description = "결재 ID", example = "1")
            Long id,

            @Schema(description = "영화 예매 내역 ID", example = "1")
            Long reservationId,

            @Schema(description = "결재자 아이디", example = "1")
            Long userId,

            @Schema(description = "결재 금액", example = "1000")
            Integer price
    ) {
        public PaymentResponse(Payment payment) {
            this(
                    payment.getId(),
                    payment.getReservationId(),
                    payment.getUserId(),
                    payment.getAmount()
            );
        }
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
