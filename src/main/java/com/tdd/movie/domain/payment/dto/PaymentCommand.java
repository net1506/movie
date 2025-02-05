package com.tdd.movie.domain.payment.dto;

import com.tdd.movie.domain.support.error.CoreException;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.RESERVATION_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.User.*;

public class PaymentCommand {

    public record CreatePaymentCommand(
            @Schema(description = "영화관 예매 내역 ID", example = "1")
            Long reservationId,
            @Schema(description = "사용자 ID", example = "1")
            Long userId,
            @Schema(description = "결재 금액", example = "15000")
            Integer amount
    ) {
        public CreatePaymentCommand {
            if (reservationId == null) {
                throw new CoreException(RESERVATION_ID_MUST_NOT_BE_NULL);
            }

            if (userId == null) {
                throw new CoreException(USER_ID_MUST_NOT_BE_NULL);
            }

            if (amount == null) {
                throw new CoreException(AMOUNT_MUST_NOT_BE_NULL);
            }

            if (amount <= 0) {
                throw new CoreException(AMOUNT_MUST_BE_POSITIVE);
            }
        }
    }

}
