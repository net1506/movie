package com.tdd.movie.domain.payment.dto;

public class PaymentRepositoryParam {

    public record GetPaymentByIdParam(
            Long id
    ) {

    }
}
