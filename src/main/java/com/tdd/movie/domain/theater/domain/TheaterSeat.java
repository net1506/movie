package com.tdd.movie.domain.theater.domain;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "theater_seats")
@NoArgsConstructor
@Getter
public class TheaterSeat extends BaseEntity {
    private Long theaterScheduleId;
    private Integer number;
    private Integer price;
    private Boolean isReserved;

    @Builder
    public TheaterSeat(Long id, Long theaterScheduleId, Integer number, Integer price, Boolean isReserved, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.theaterScheduleId = theaterScheduleId;
        this.number = number;
        this.price = price;
        this.isReserved = isReserved;
    }
}
