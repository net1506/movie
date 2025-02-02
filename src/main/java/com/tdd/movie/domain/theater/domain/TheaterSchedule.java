package com.tdd.movie.domain.theater.domain;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "theater_schedules")
@NoArgsConstructor
@Getter
public class TheaterSchedule extends BaseEntity {

    private Long movieId;
    private Long theaterId;
    private Long theaterScreenId;
    private LocalDateTime movieAt;
    private LocalDateTime reservationStartAt;
    private LocalDateTime reservationEndAt;

    @Builder
    public TheaterSchedule(Long id, Long movieId, Long theaterId, Long theaterScreenId, LocalDateTime movieAt, LocalDateTime reservationStartAt, LocalDateTime reservationEndAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.theaterScreenId = theaterScreenId;
        this.movieAt = movieAt;
        this.reservationStartAt = reservationStartAt;
        this.reservationEndAt = reservationEndAt;
    }
}
