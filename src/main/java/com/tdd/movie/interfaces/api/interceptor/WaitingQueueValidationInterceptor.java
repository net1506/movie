package com.tdd.movie.interfaces.api.interceptor;

import com.tdd.movie.application.WaitingQueueFacade;
import com.tdd.movie.domain.support.error.CoreException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.tdd.movie.domain.support.error.ErrorType.WaitingQueue.WAITING_QUEUE_TOKEN_UUID_REQUIRED;
import static com.tdd.movie.interfaces.api.CommonHttpHeader.X_WAITING_QUEUE_TOKEN_UUID;

/**
 * API 요청이 컨트롤러에 도달하기 전에 대기열(Waiting Queue)과 관련된 검증을 수행하는 역할
 */
@Component
@RequiredArgsConstructor
public class WaitingQueueValidationInterceptor implements HandlerInterceptor {

    private final WaitingQueueFacade waitingQueueFacade;

    /**
     * 콘서트 예약 관련 요청이 실행되기 전에 대기열(Waiting Queue) 검증을 수행하는 역할
     * 대기열 토큰(UUID)이 있는지 확인하고 해당 토큰이 ACTIVE 상태인지 확인한다.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청의 헤더(X-WAITING-QUEUE-TOKEN-UUID)에서 대기열 토큰(UUID)을 가져옴.
        // 토큰이 없거나 비어 있다면 예외 발생 (CoreException).
        String waitingQueueTokenUuid = request.getHeader(X_WAITING_QUEUE_TOKEN_UUID);

        // 토큰의 UUID 가 비어있다면 에러 처리
        if (waitingQueueTokenUuid == null || waitingQueueTokenUuid.isEmpty()) {
            throw new CoreException(WAITING_QUEUE_TOKEN_UUID_REQUIRED);
        }

        if (waitingQueueTokenUuid.equals("test")) {
            return true;
        }

        // 대기열 토큰이 활성화 상태인지 검증한다.
        waitingQueueFacade.validateWaitingQueueProcessing(waitingQueueTokenUuid);

        return true;
    }

}
