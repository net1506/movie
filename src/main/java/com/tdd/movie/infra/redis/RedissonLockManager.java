package com.tdd.movie.infra.redis;

import com.tdd.movie.domain.support.LockManager;
import com.tdd.movie.domain.support.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.tdd.movie.domain.support.error.ErrorType.FAILED_TO_ACQUIRE_LOCK;

/**
 * Redis 기반의 분산 락을 관리하는 클래스.
 * Redisson 을 사용하여 락을 획득하고, 지정된 작업을 실행한 후 락을 해제함.
 *
 * <p>이 클래스는 `LockManager` 인터페이스를 구현하며,
 * `@DistributedLock` 어노테이션이 적용된 메서드에서 사용됨.</p>
 *
 * <p>동작 방식:</p>
 * <ol>
 *     <li>주어진 `lockName`을 사용해 Redis 에서 락을 시도</li>
 *     <li>락을 획득하면 지정된 작업(`operation`)을 실행</li>
 *     <li>락이 만료되거나 작업이 끝나면 락을 해제</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class RedissonLockManager implements LockManager {

    /**
     * 락을 시도할 최대 대기 시간 (초 단위).
     * 즉, 락이 사용 중이면 최대 5초 동안 대기 후 실패 처리.
     */
    private static final long WAIT_TIME = 5L;
    /**
     * 락을 획득한 후 유지되는 시간 (초 단위).
     * 즉, 락을 획득한 후 3초 동안 유지됨.
     */
    private static final long LEASE_TIME = 3L;
    /**
     * 시간 단위를 초(SECONDS)로 설정.
     */
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * Redisson 클라이언트 인스턴스.
     * Redis에서 분산 락을 관리하는 역할을 함.
     */
    private final RedissonClient redissonClient;

    /**
     * 분산 락을 획득한 후 지정된 작업을 실행하는 메서드.
     *
     * <p>트랜잭션을 사용하지 않도록 `@Transactional(propagation = Propagation.NEVER)` 설정.</p>
     * - 트랜잭션 내부에서 실행되지 않도록 강제함.
     * - 트랜잭션이 열린 상태에서 실행되면 예외 발생 → 락과 트랜잭션이 충돌하는 문제 방지.
     * - 락을 트랜잭션과 분리하여 독립적으로 관리 가능.
     *
     * @param lockName  락을 설정할 키 값
     * @param operation 실행할 작업 (람다 또는 메서드 참조)
     * @return 작업 실행 결과
     * @throws Throwable 작업 실행 중 발생한 예외
     */
    @Transactional(propagation = Propagation.NEVER)
    @Override
    public Object lock(String lockName, Supplier<Object> operation) throws Throwable {
        // Redis 에서 락 객체(RLock) 가져오기
        RLock rLock = redissonClient.getLock(lockName);

        try {
            // 락을 획득 (최대 WAIT_TIME 동안 기다리고, LEASE_TIME 동안 유지)
            // 1. 현재 실행 중인 프로세스(쓰레드)가 해당 락을 가져올 수 있는지 확인.
            // 2. 다른 프로세스(쓰레드)가 이미 이 락을 점유하고 있으면 WAIT_TIME 만큼 대기.
            // 3. 기다리는 동안 락이 해제되면, 락을 획득하고 LEASE_TIME 동안 유지.
            // 4. 만약 WAIT_TIME 이 지나도 락을 획득하지 못하면 false 반환 (즉, 타임아웃 발생).
            boolean available = rLock.tryLock(WAIT_TIME, LEASE_TIME, TIME_UNIT);

            // 락 획득 실패 시 예외 발생
            if (!available) {
                throw new CoreException(FAILED_TO_ACQUIRE_LOCK);
            }

            // 지정된 작업 실행
            return operation.get();
        } catch (InterruptedException e) {
            // 쓰레드 인터럽트 예외 발생
            throw new InterruptedException();
        } finally {
            // 락 해제 (try-finally 로 보장)
            if (rLock != null && rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
