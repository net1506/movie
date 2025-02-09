package com.tdd.movie.interfaces.consumer;

import com.tdd.movie.domain.payment.event.PaymentSuccessEvent;
import com.tdd.movie.domain.send.DataPlatformSendService;
import com.tdd.movie.domain.support.EventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    @Getter
    private static final List<String> messages = new ArrayList<>();
    private final DataPlatformSendService sendService;

    @KafkaListener(topics = EventType.PAYMENT_SUCCESS, groupId = "payment-group")
    public void consume(Long paymentId) {
        log.info("Consumed message: {}", paymentId);
        sendService.send(new PaymentSuccessEvent(paymentId));
        messages.add(paymentId.toString());
    }

}