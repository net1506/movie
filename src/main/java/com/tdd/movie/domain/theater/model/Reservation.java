package com.tdd.movie.domain.theater.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import com.tdd.movie.domain.support.error.CoreException;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.*;
import static com.tdd.movie.domain.theater.model.ReservationStatus.CANCELED;
import static com.tdd.movie.domain.theater.model.ReservationStatus.CONFIRMED;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_user_seat", columnList = "userId, theaterSeatId")
})
@NoArgsConstructor
@Getter
public class Reservation extends BaseEntity {

    private Long theaterSeatId;

    private Long userId;

    private ReservationStatus status;

    private LocalDateTime reservedAt;

    @Builder
    public Reservation(Long id, Long theaterSeatId, Long userId, ReservationStatus status, LocalDateTime reservedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.theaterSeatId = theaterSeatId;
        this.userId = userId;
        this.status = status;
        this.reservedAt = reservedAt;
    }

    public void validateReservationOwner(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new CoreException(RESERVATION_USER_NOT_MATCHED);
        }
    }

    public void validatePaymentStatus() {
        if (this.status.equals(CONFIRMED)) {
            throw new CoreException(RESERVATION_ALREADY_PAID);
        }

        if (this.status.equals(CANCELED)) {
            throw new CoreException(RESERVATION_ALREADY_CANCELED);
        }
    }

    // 예약 하기
    public void confirm() {
        if (status == CONFIRMED) {
            throw new CoreException(RESERVATION_ALREADY_PAID);
        }

        if (status == CANCELED) {
            throw new CoreException(RESERVATION_ALREADY_CANCELED);
        }

        this.status = CONFIRMED;
    }

    // 취소하기
    public void cancel() {
        if (status == CANCELED) {
            throw new CoreException(RESERVATION_ALREADY_CANCELED);
        }

        if (status == CONFIRMED) {
            throw new CoreException(RESERVATION_ALREADY_PAID);
        }

        this.status = CANCELED;
    }
}
