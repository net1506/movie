package com.tdd.movie.interfaces.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka // Kafka를 사용하기 위한 설정 활성화
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    // application.yml 에서 Kafka 설정을 읽어옴
    private final KafkaProperties kafkaProperties;

    // Consumer 설정을 만드는 Bean
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        log.info("Initializing Kafka Consumer Factory");
        log.info("BootstrapServers : " + kafkaProperties.getBootstrapServers());
        log.info("GroupId : " + kafkaProperties.getConsumer().getGroupId());

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()); // Kafka 서버 주소
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId()); // Consumer Group ID
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 메시지 키 역직렬화
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 메시지 값 역직렬화

        return new DefaultKafkaConsumerFactory<>(config);
    }

    // Kafka 메시지를 받는 리스너 컨테이너 설정
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory()); // 위에서 만든 consumerFactory()를 사용

        return factory;
    }
}