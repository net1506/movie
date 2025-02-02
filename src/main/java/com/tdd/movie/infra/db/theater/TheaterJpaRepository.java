package com.tdd.movie.infra.db.theater;

import com.tdd.movie.domain.theater.domain.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterJpaRepository extends JpaRepository<Theater, Long> {
}
