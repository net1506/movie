package com.tdd.movie.domain.payment.dto;

import com.tdd.movie.domain.support.error.CoreException;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.tdd.movie.domain.support.error.ErrorType.Payment.PAYMENT_ID_MUST_NOT_BE_NULL;

public class PaymentQuery {

    public record GetPaymentByIdQuery(
            @Schema(description = "결재 내역 아이디", example = "1")
            Long id
    ) {
        public GetPaymentByIdQuery {
            if (id == null) {
                throw new CoreException(PAYMENT_ID_MUST_NOT_BE_NULL);
            }
        }
    }


}
