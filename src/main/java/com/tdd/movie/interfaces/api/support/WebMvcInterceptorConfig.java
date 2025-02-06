package com.tdd.movie.interfaces.api.support;

import com.tdd.movie.interfaces.api.interceptor.WaitingQueueValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcInterceptorConfig implements WebMvcConfigurer {

    private final WaitingQueueValidationInterceptor waitingQueueValidationInterceptor;

    /**
     * 특정 API 요청이 들어올 때 인터셉터(WaitingQueueValidationInterceptor)가 실행되도록 설정하는 역할
     * 인터셉터를 적용할 API 경로 지정.
     * 아래 4가지 경로 패턴에 대해 인터셉터를 적용.
     * <p>
     * /api/v1/movies/**             → 영화 관련 API
     * /api/v1/theaters/**             → 영화관 관련 API
     * /api/v1/theater-seats/**        → 영화관 좌석 관련 API
     * /api/v1/theater-schedules/**    → 영화관 일정 관련 API
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(waitingQueueValidationInterceptor)
                .addPathPatterns(
                        "/api/v1/movies/**",
                        "/api/v1/theaters/**",
                        "/api/v1/theater-seats/**",
                        "/api/v1/theater-schedules/**"
                );
    }
}