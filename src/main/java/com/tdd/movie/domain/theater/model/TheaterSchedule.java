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

import static com.tdd.movie.domain.support.error.ErrorType.Theater.THEATER_SCHEDULE_NOT_RESERVABLE;
import static java.time.LocalDateTime.now;

@Entity
@Table(name = "theater_schedules", indexes = {
        @Index(name = "idx_movie_theater", columnList = "movieId, theaterId"),
        @Index(name = "idx_reservation_period", columnList = "reservationStartAt, reservationEndAt")
})
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

    // 예약이 가능한 기간인지 검증
    public void validateReservablePeriod() {
        // 현재 시간이 예약 시작 시간 이전이거나, 예약 종료 시간을 초과한 경우 예외 발생
        if (reservationStartAt.isAfter(now()) || reservationEndAt.isBefore(now())) {
            throw new CoreException(THEATER_SCHEDULE_NOT_RESERVABLE);
        }
    }
}
