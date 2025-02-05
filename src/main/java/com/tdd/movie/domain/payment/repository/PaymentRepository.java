package com.tdd.movie.domain.payment.repository;

import com.tdd.movie.domain.payment.dto.PaymentRepositoryParam.GetPaymentByIdParam;
import com.tdd.movie.domain.payment.model.Payment;

public interface PaymentRepository {

    Payment savePayment(Payment payment);

    Payment getPayment(GetPaymentByIdParam param);

}
