package com.tdd.movie;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootApplication
public class MovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieApplication.class, args);

        LocalDateTime now = LocalDateTime.now();
        System.out.println("현재시간 " + now);
    }

    @PostConstruct
    public void init() {
        // timezone 설정
        // https://isntyet.github.io/java/Spring-boot-Timezone-%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0/ 참고
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
