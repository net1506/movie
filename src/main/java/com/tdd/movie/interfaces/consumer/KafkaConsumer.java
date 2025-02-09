package com.tdd.movie.interfaces.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KafkaConsumer {

    @Getter
    private static final List<String> messages = new ArrayList<>();

    // Kafka 에서 "my-topic" 토픽에 메시지가 오면 자동으로 실행되는 메서드
    @KafkaListener(
            topics = "${spring.kafka.template.default-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(@Payload String message, @Headers MessageHeaders messageHeaders) {
        System.out.println("Received Kafka message : " + message);
        log.info("consumer: success >>> message: {}, headers: {}", message, messageHeaders);

        messages.add(message);
    }
}
