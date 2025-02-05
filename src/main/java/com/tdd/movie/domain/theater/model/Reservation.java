package com.tdd.movie.domain.theater.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
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
}
