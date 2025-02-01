package com.tdd.movie.domain.support.aop;

import com.tdd.movie.domain.support.LockManager;
import com.tdd.movie.domain.support.annotaion.DistributedLock;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 분산 락을 적용하는 AOP 클래스
 * <p>
 * 특정 메서드에 `@DistributedLock` 어노테이션이 붙어 있으면,
 * 해당 메서드가 실행되기 전에 락을 획득한 후 실행되도록 처리함.
 * <p>
 * `@Order(Ordered.HIGHEST_PRECEDENCE + 1)` → AOP 실행 순서를 지정 (가장 우선순위 높음)
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@RequiredArgsConstructor
public class DistributedLockAop {
    /**
     * 분산 락을 관리하는 LockManager 객체
     */
    private final LockManager lockManager;

    /**
     * `@DistributedLock` 어노테이션이 적용된 메서드를 감싸는 AOP 메서드
     *
     * @param joinPoint       실행되는 메서드의 정보
     * @param distributedLock `@DistributedLock` 어노테이션 정보를 가져옴
     * @return 메서드 실행 결과
     * @throws Throwable 예외 발생 시 전달
     */
    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        // 동적으로 생성한 락 키
        String dynamicKey = createDynamicKey(joinPoint, distributedLock.keys());
        String lockName = distributedLock.type().lockName() + ":" + dynamicKey; // ex) "userWalletLock:ORD-5678"

        return lockManager.lock(lockName, () -> {
            try {
                return joinPoint.proceed();
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * AOP 에서 메서드의 매개변수를 이용해 동적 락 키를 생성하는 메서드
     *
     * @param joinPoint 실행된 메서드의 정보
     * @param keys      `@DistributedLock`에서 지정한 키 목록
     * @return 생성된 동적 키 값 (예: "ORD-5678 또는 1234:ORD-5678")
     */
    private String createDynamicKey(ProceedingJoinPoint joinPoint, String[] keys) {
        // 실행된 메서드의 시그니처를 가져옴
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // 메서드의 매개변수 이름 가져오기
        // public void processPayment(Long userId, String orderId)
        String[] methodParameterNames = methodSignature.getParameterNames(); // ["userId", "orderId"]

        // 실제 전달된 매개변수 값 가져오기
        // processPayment(1234, "ORD-5678")가 실행된다고 가정.
        Object[] methodArgs = joinPoint.getArgs(); // [1234, "ORD-5678"]

        // @DistributedLock 의 keys 배열에서 지정한 값들을 조합하여 락 키 생성
        return Arrays.stream(keys)
                .map(key -> {
                    // keys 에서 지정한 이름이 메서드 매개변수 중 어디에 위치하는지 찾음
                    int indexOfKey = Arrays.asList(methodParameterNames).indexOf(key);

                    // 매개변수가 없거나 null이면 예외 발생
                    if (indexOfKey == -1 || methodArgs[indexOfKey] == null) {
                        throw new CoreException(ErrorType.KEY_NOT_FOUND_OR_NULL);
                    }

                    // 해당 매개변수의 값을 문자열로 변환하여 반환
                    return methodArgs[indexOfKey].toString();
                })
                .collect(Collectors.joining(":"));
    }

}
