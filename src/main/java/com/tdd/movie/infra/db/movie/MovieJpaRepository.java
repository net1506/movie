package com.tdd.movie.infra.db.movie;

import com.tdd.movie.domain.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MovieJpaRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE m.screeningStartDate <= :date AND m.screeningEndDate >= :date")
    List<Movie> findAllByScreeningPeriod(LocalDate date);

    @Query("SELECT m FROM Movie m WHERE m.screeningStartDate > :date")
    List<Movie> findAllByScreeningStartDateAfter(LocalDate date);
}
