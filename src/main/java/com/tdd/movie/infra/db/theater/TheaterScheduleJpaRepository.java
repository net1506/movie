package com.tdd.movie.infra.db.theater;

import com.tdd.movie.domain.theater.model.TheaterSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TheaterScheduleJpaRepository extends JpaRepository<TheaterSchedule, Long> {
    @Query("SELECT DISTINCT ts.theaterId FROM TheaterSchedule ts WHERE ts.movieId = :movieId")
    List<Long> findDistinctTheaterIdsByMovieId(Long movieId);

    @Query("SELECT ts FROM TheaterSchedule ts WHERE ts.theaterId = :theaterId AND ts.movieId = :movieId AND ts.reservationStartAt <= :now AND ts.reservationEndAt >= :now")
    List<TheaterSchedule> findByTheaterIdAndMovieIdAndReservationPeriod(Long theaterId, Long movieId, LocalDateTime now);
}
