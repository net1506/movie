package com.tdd.movie.domain.theater.domain;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "screens")
@NoArgsConstructor
@Getter
public class TheaterScreen extends BaseEntity {

    private Integer number; // 상영관 번호

    private Long theaterId;

    private Long seatCount;
}
