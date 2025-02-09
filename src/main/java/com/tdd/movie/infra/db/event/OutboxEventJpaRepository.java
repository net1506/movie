package com.tdd.movie.infra.db.event;

import com.tdd.movie.domain.event.model.OutboxEvent;
import com.tdd.movie.domain.event.model.OutboxEventStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OutboxEvent e WHERE e.id = :id")
    Optional<OutboxEvent> findByIdWithLock(Long id);

    Optional<OutboxEvent> findByStatus(OutboxEventStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = :status AND e.retryCount < :maxRetryCount AND e.retryAt < :retryAtBefore")
    List<OutboxEvent> findAllByStatusAndRetryCountAndRetryAtBefore(OutboxEventStatus status,
                                                                   int maxRetryCount, LocalDateTime retryAtBefore);
}