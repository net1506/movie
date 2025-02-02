package com.tdd.movie.domain.theater.domain;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

}
