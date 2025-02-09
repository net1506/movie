package com.tdd.movie.infra.db.kafka;

import com.tdd.movie.domain.event.EventPublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate; // Kafka 메시지를 보내는 템플릿

    @Value("${spring.kafka.template.default-topic}")
    private String topicName;

    @PostConstruct
    public void init() {
        log.info("Initializing kafka producer...");
        log.info(topicName); // my-topic
    }

    // Kafka 특정 토픽으로 메시지를 보내는 메서드
    @Override
    public void publish(String contentMessage) {
        Message<String> message = MessageBuilder
                .withPayload(contentMessage)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);

        printSendResult(future);
    }

    // Kafka 특정 토픽으로 메시지를 보내는 메서드
    @Override
    public void publish(String topic, String contentMessage) {
        Message<String> message = MessageBuilder
                .withPayload(contentMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);

        printSendResult(future);
    }

    private static void printSendResult(CompletableFuture<SendResult<String, String>> future) {
        // future.whenComplete() 에서는 메세지 전달이 성공 or 실패했을 때 어떤 동작을 할지 설정할 수 있다.
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("producer: success >>> message: {}, offset: {}",
                        result.getProducerRecord().value().toString(), result.getRecordMetadata().offset());
            } else {
                log.info("producer: failure >>> message: {}", ex.getMessage());
            }
        });
    }
}