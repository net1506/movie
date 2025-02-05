package com.tdd.movie.infra.db.theater;

import com.tdd.movie.domain.theater.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterJpaRepository extends JpaRepository<Theater, Long> {
}
