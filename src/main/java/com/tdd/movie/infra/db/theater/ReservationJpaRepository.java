package com.tdd.movie.infra.db.theater;

import com.tdd.movie.domain.theater.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
}
