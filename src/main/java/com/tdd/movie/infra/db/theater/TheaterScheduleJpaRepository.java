package com.tdd.movie.infra.db.theater;

import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TheaterScheduleJpaRepository extends JpaRepository<TheaterSchedule, Long> {
    @Query("SELECT DISTINCT ts.theaterId FROM TheaterSchedule ts WHERE ts.movieId = :movieId")
    List<Long> findDistinctTheaterIdsByMovieId(Long movieId);
}
