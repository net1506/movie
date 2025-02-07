package com.tdd.movie.domain.payment.service;

import com.tdd.movie.domain.payment.dto.PaymentCommand.CreatePaymentCommand;
import com.tdd.movie.domain.payment.model.Payment;
import com.tdd.movie.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {

    private final PaymentRepository paymentRepository;

    public Long createPayment(CreatePaymentCommand command) {
        Payment payment = Payment.builder()
                .reservationId(command.reservationId())
                .userId(command.userId())
                .amount(command.amount())
                .build();

        return paymentRepository.savePayment(payment).getId();
    }

}
