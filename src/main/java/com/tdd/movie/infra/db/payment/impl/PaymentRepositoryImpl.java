package com.tdd.movie.infra.db.payment.impl;

import com.tdd.movie.domain.payment.dto.PaymentRepositoryParam;
import com.tdd.movie.domain.payment.model.Payment;
import com.tdd.movie.domain.payment.repository.PaymentRepository;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import com.tdd.movie.infra.db.payment.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment getPayment(PaymentRepositoryParam.GetPaymentByIdParam param) {
        return paymentJpaRepository.findById(param.id())
                .orElseThrow(() -> new CoreException(ErrorType.Payment.PAYMENT_NOT_FOUND));
    }

}
