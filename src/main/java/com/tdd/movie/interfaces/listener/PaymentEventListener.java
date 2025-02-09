package com.tdd.movie.interfaces.listener;

import com.tdd.movie.domain.payment.event.PaymentSuccessEvent;
import com.tdd.movie.domain.send.DataPlatformSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Deprecated
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final DataPlatformSendService sendService;
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        sendService.send(event);
    }
}