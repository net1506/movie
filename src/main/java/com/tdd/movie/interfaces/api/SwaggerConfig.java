package com.tdd.movie.interfaces.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정을 커스터마이징하는 Bean 을 정의
     * 이 메서드는 Swagger UI 에서 API 보안을 구성하기 위해 사용된다.
     * 'X-Queue-Token' 이라는 API Key 기반의 인증 방식을 추가합니다.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Swagger UI 에서 보안 요구 사항을 추가한다.
                .addSecurityItem(
                        new SecurityRequirement().addList(CommonHttpHeader.X_WAITING_QUEUE_TOKEN_UUID)) // 'X-Waiting-Queue-Token-uuid' 보안 스키마를 요구사항에 추가
                // 'X-Waiting-Queue-Token-uuid' 이라는 이름의 보안 스키마를 정의한다.
                .components(new Components()
                        .addSecuritySchemes(CommonHttpHeader.X_WAITING_QUEUE_TOKEN_UUID, createQueueTokenScheme()));
    }


    private SecurityScheme createQueueTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) // API Key 인증 방식을 사용
                .in(SecurityScheme.In.HEADER) // 인증 값은 HTTP 헤더에서 전달됨
                .name(CommonHttpHeader.X_WAITING_QUEUE_TOKEN_UUID); // 헤더 이름은 'X-Waiting-Queue-Token-uuid' 으로 지정
    }
}
