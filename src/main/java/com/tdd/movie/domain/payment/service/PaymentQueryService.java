package com.tdd.movie.domain.payment.service;

import com.tdd.movie.domain.payment.dto.PaymentQuery.GetPaymentByIdQuery;
import com.tdd.movie.domain.payment.dto.PaymentRepositoryParam.GetPaymentByIdParam;
import com.tdd.movie.domain.payment.model.Payment;
import com.tdd.movie.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public Payment getPayment(GetPaymentByIdQuery query) {
        return paymentRepository.getPayment(new GetPaymentByIdParam(query.id()));
    }

}
