package com.tdd.movie.infra.db.theater;

import com.tdd.movie.domain.theater.model.Reservation;
import com.tdd.movie.domain.theater.model.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    // 예약 대기(WAITING) 상태인데, 일정 시간이 지나 만료된 예약을 조회한다.
    // - 만료 기준 시간(expiredAt) 이전에 예약된 좌석 중 결제가 완료되지 않은(WAITING 상태) 예약만 조회.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.reservedAt < :expiredAt")
    List<Reservation> findAllByStatusAndReservedAtBefore(
            ReservationStatus status,
            LocalDateTime expiredAt
    );

    // 여러 개의 예약 ID 목록에 대해 비관적 락(PESSIMISTIC_WRITE)을 걸어 예약 정보를 조회한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id IN :ids")
    List<Reservation> findAllByIdsWithLock(List<Long> ids);
}
