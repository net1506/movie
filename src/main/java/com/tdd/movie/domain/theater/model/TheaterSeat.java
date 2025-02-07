package com.tdd.movie.domain.theater.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import com.tdd.movie.domain.support.error.CoreException;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_SEAT_ALREADY_RESERVED;
import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_SEAT_NOT_RESERVED;

@Entity
@Table(name = "theater_seats", indexes = {
        @Index(name = "idx_schedule_reserved", columnList = "theaterScheduleId, isReserved")
})
@NoArgsConstructor
@Getter
public class TheaterSeat extends BaseEntity {
    private Long theaterScheduleId;
    private Integer number;
    private Integer price;
    private Boolean isReserved;
    @Version
    private Long version; // 같은 좌석을 두 개 이상의 트랜잭션이 동시에 업데이트하려 할 때 ObjectOptimisticLockingFailureException 가 발생함.

    @Builder
    public TheaterSeat(Long id, Long theaterScheduleId, Integer number, Integer price, Boolean isReserved, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.theaterScheduleId = theaterScheduleId;
        this.number = number;
        this.price = price;
        this.isReserved = isReserved;
    }

    public void reserve() {
        if (isReserved) {
            throw new CoreException(THEATER_SEAT_ALREADY_RESERVED);
        }

        this.isReserved = true;
    }

    // 좌석 예매 취소
    public void release() {
        if (!this.isReserved) {
            throw new CoreException(THEATER_SEAT_NOT_RESERVED);
        }

        this.isReserved = false;
    }
}
