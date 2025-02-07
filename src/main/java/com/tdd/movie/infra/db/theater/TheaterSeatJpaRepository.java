package com.tdd.movie.infra.db.theater;

import com.tdd.movie.domain.theater.model.TheaterSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TheaterSeatJpaRepository extends JpaRepository<TheaterSeat, Long> {
    @Query("SELECT ts FROM TheaterSeat ts WHERE ts.theaterScheduleId = :theaterScheduleId AND ts.isReserved = :isReserved")
    List<TheaterSeat> findAllByTheaterScheduleIdAndIsReserved(Long theaterScheduleId, Boolean isReserved);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ts from TheaterSeat ts where ts.id in :ids")
    List<TheaterSeat> findAllByIdsWithLock(List<Long> ids);
}
