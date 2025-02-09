package com.tdd.movie.infra.db.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    // yml 파일에 있는 kafka 속성을 읽어옴
    private final KafkaProperties kafkaProperties;

    @PostConstruct
    public void setUpPrint() {
        log.info("Properties : {}", kafkaProperties.getProperties());
        log.info("BootstrapServers : {}", kafkaProperties.getBootstrapServers());
        log.info("GroupId : {}", kafkaProperties.getConsumer().getGroupId());
        log.info("EnableAutoCommit : {}", kafkaProperties.getConsumer().getEnableAutoCommit());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()); // Kafka 서버 주소
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // 메시지 키 직렬화
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // 메시지 값 직렬화

        return new DefaultKafkaProducerFactory<>(config);
    }

    // Kafka 메시지를 보내는 KafkaTemplate을 생성하는 Bean
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}