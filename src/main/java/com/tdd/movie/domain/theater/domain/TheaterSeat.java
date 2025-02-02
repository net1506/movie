package com.tdd.movie.domain.theater.domain;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "theater_seats")
@NoArgsConstructor
@Getter
public class TheaterSeat extends BaseEntity {
}
