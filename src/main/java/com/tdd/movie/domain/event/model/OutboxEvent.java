package com.tdd.movie.domain.event.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import com.tdd.movie.domain.event.EventConstants;
import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * **OutboxEvent 엔티티**
 * - Kafka 또는 다른 비동기 시스템으로 보낼 이벤트를 데이터베이스에 저장하고 관리
 * - 이벤트 상태(처리됨, 실패, 재시도 중 등)를 관리하며, 재시도 로직 포함
 */
@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor
public class OutboxEvent extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status; // 현재 이벤트 상태 (PENDING, PUBLISHED, FAILED)

    @Column(nullable = false)
    private String type; // 이벤트 타입(TOPIC)

    @Column(nullable = false)
    private String payload; // (MESSAGE)

    @Column(nullable = false)
    private int retryCount = 0; // 실패 후 재시도 횟수

    private LocalDateTime retryAt; // 다음 재시도 가능 시간

    @Builder
    public OutboxEvent(Long id, OutboxEventStatus status, String type, String payload,
                       int retryCount,
                       LocalDateTime retryAt,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.status = status;
        this.type = type;
        this.payload = payload;
        this.retryCount = retryCount;
        this.retryAt = retryAt;
    }

    /**
     * **이벤트를 발행 상태로 변경**
     * - 이벤트가 이미 발행된 경우 예외 발생
     * - 이벤트 상태를 "PUBLISHED"로 변경하여 전송 완료로 표시
     */
    public void publish() {
        if (this.status == OutboxEventStatus.PUBLISHED) {
            throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_ALREADY_PUBLISHED);
        }

        this.status = OutboxEventStatus.PUBLISHED;
    }

    /**
     * **이벤트 발행 실패 처리**
     * - 이벤트가 이미 실패 상태라면 예외 발생
     * - 상태를 "FAILED"로 변경하고, 다음 재시도 시간을 설정
     */
    public void fail() {
        if (this.status == OutboxEventStatus.FAILED) {
            throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_ALREADY_FAILED);
        }

        this.status = OutboxEventStatus.FAILED;
        this.retryAt = LocalDateTime.now().plusMinutes(EventConstants.RETRY_INTERVAL_MINUTES);
    }

    /**
     * **이벤트 재시도 처리**
     * - 실패 상태가 아닌 경우 재시도 불가 (예외 발생)
     * - 최대 재시도 횟수를 초과하면 예외 발생
     * - 설정된 재시도 가능 시간이 지나지 않았다면 예외 발생
     * - 상태를 "PENDING"으로 변경하여 다시 처리 가능하도록 함
     */
    public void retry() {
        if (this.status != OutboxEventStatus.FAILED) {
            throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_NOT_FAILED);
        }
        if (this.retryCount >= EventConstants.MAX_RETRY_COUNT) {
            throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_RETRY_EXCEEDED);
        }
        if (this.retryAt == null) {
            throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_UPDATED_AT_NULL);
        }
        LocalDateTime now = LocalDateTime.now();
        // retryAt < now 이어야 재시도가 가능함
        if (this.retryAt.isAfter(now)) {
            // 아웃박스 이벤트 재시도 간격이 지나지 않았습니다.
            throw new CoreException(ErrorType.OutboxEvent.OUTBOX_EVENT_RETRY_INTERVAL_NOT_PASSED);
        }

        this.retryCount++;
        this.status = OutboxEventStatus.PENDING;
    }
}

