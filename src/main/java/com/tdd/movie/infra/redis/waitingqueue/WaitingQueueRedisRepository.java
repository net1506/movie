package com.tdd.movie.infra.redis.waitingqueue;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import com.tdd.movie.domain.waitingqueue.dto.WaitingQueueRepositoryParam;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueue;
import com.tdd.movie.domain.waitingqueue.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.ofEpochSecond;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRedisRepository implements WaitingQueueRepository {

    private final RedisTemplate<String, Object> redisTemplate; // Redis 데이터 조작을 위한 템플릿

    @Value("${queue.waiting-key}")
    private String waitingQueueKey; // 대기열을 위한 Redis 키 (waiting)

    @Value("${queue.active-key}")
    private String activeQueueKey; // 활성화된 대기열을 위한 Redis 키 (active)

    @Override
    public String addWaitingQueue(String uuid) {
        boolean added = redisTemplate.opsForZSet()
                .add(waitingQueueKey, uuid, now().toEpochSecond(ZoneOffset.UTC));
        return added ? uuid : null;
    }

    @Override
    public void activateWaitingQueues(WaitingQueueRepositoryParam.ActivateWaitingQueuesParam param) {
        redisTemplate.opsForZSet()
                // rank 0 ~ 10: 스코어가 제일 작은것 부터 1 - 10 등 까지 조회
                // revrank 0 ~ 10: 스코어가 제일 큰것 부터 1 - 10 등 까지 조회
                .range(waitingQueueKey, 0, param.availableSlots() - 1)
                .forEach(uuid -> {
                    long expirationTimestamp = now()
                            .plus(param.timeout(), param.unit().toChronoUnit()) // 5 MINUTE
                            .toEpochSecond(ZoneOffset.UTC);

                    // 추가 (activeQueueKey 에는 추가)
                    redisTemplate.opsForZSet().add(activeQueueKey, uuid, expirationTimestamp);

                    // 삭제 (waitingQueueKey 에서는 제거)
                    redisTemplate.opsForZSet().remove(waitingQueueKey, uuid);
                });
    }

    @Override
    public Long getWaitingQueuePosition(WaitingQueueRepositoryParam.GetWaitingQueuePositionByUuidParam param) {
        // 대기열 순번
        Long rank = redisTemplate.opsForZSet().rank(waitingQueueKey, param.uuid());
        if (rank == null) {
            // 이미 대기열이 활성화 되어 있는 경우에는 0 으로 반환
            if (redisTemplate.opsForZSet().rank(activeQueueKey, param.uuid()) != null) {
                return 0L;
            }

            throw new CoreException(ErrorType.WaitingQueue.WAITING_QUEUE_NOT_FOUND);
        }
        return rank + 1;
    }

    @Override
    public WaitingQueue getActiveToken(WaitingQueueRepositoryParam.GetActiveTokenByUuidParam param) {
        Double score = redisTemplate.opsForZSet().score(activeQueueKey, param.uuid());

        if (score == null) {
            throw new CoreException(ErrorType.WaitingQueue.ACTIVE_QUEUE_NOT_FOUND);
        }

        return WaitingQueue.builder()
                .uuid(param.uuid())
                .expiredAt(ofEpochSecond(score.longValue(), 0, ZoneOffset.UTC))
                .build();
    }

    @Override
    public List<WaitingQueue> getAllActiveTokens() {
        return redisTemplate.opsForZSet()
                .range(activeQueueKey, 0, -1)
                .stream()
                .map(uuid -> WaitingQueue.builder()
                        .uuid(uuid.toString())
                        .expiredAt(
                                ofEpochSecond(
                                        redisTemplate.opsForZSet().score(activeQueueKey, uuid).longValue(),
                                        0,
                                        ZoneOffset.UTC
                                )
                        )
                        .build())
                .toList();
    }

    @Override
    public void removeActiveToken(WaitingQueueRepositoryParam.RemoveActiveTokenParam param) {
        redisTemplate.opsForZSet().remove(activeQueueKey, param.uuid());
    }
}
