package com.tdd.movie.domain.send;

import com.tdd.movie.domain.payment.event.PaymentSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformSendService {

    // 결제 성공 데이터를 전송하는 메서드
    public void send(PaymentSuccessEvent event) {
        log.info("Send to data platform: {}", event);
    }

}
