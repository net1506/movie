package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.payment.model.Payment;
import io.swagger.v3.oas.annotations.media.Schema;

public class ReservationControllerDto {

    public record PayReservationResponse(
            PaymentResponse paymentResponse
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
}
