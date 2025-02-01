package com.tdd.movie.domain.support.annotaion;

import com.tdd.movie.domain.support.DistributedLockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 분산 락(Distributed Lock)을 적용하기 위한 어노테이션입니다.
 * <p>
 * 해당 어노테이션이 적용된 메서드는 분산 락을 획득한 후 실행됩니다.
 *
 * <p>속성 설명:</p>
 * <ul>
 *     <li>{@code type} - 사용할 분산 락의 타입을 지정합니다.</li>
 *     <li>{@code keys} - 락을 획득할 키 값을 지정합니다.</li>
 * </ul>
 * <p>
 * 사용 예시:
 * <pre>
 * {@code
 *      @DistributedLock(type = DistributedLockType.USER_WALLET, keys = {"userId"})
 *      public void processPayment(Long userId) {
 *          // 이 메서드는 해당 userId 에 대한 락을 획득한 후 실행됨
 *      }
 * }
 */
// RetentionPolicy.RUNTIME → 런타임까지 어노테이션이 유지됨.
@Retention(RetentionPolicy.RUNTIME)
// ElementType.METHOD → 메서드에만 적용 가능하다는 의미.
@Target(ElementType.METHOD)
public @interface DistributedLock {
    /**
     * 사용할 분산 락의 타입을 지정하는 필드입니다.
     */
    DistributedLockType type();

    /**
     * 분산 락을 적용할 키 목록입니다.
     * 락을 획득할 때 사용되며, 특정 리소스를 구분하는 데 활용됩니다.
     */
    String[] keys();
}
