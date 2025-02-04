package com.tdd.movie.domain.theater.repository;

import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam;
import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.domain.TheaterSchedule;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindTheatersByIdsParam;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.GetTheaterByIdParam;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

import static com.tdd.movie.domain.support.CacheName.THEATER;

public interface TheaterRepository {

    @Cacheable(value = THEATER, key = "#param.theaterId")
    Theater getTheater(GetTheaterByIdParam query);

    List<Theater> findTheaters(FindTheatersByIdsParam param);

    List<Long> findDistinctTheaterIds(FindDistinctTheaterIdsByMovieIdParam param);

    List<TheaterSchedule> findAllTheaterSchedules(FindAllTheaterSchedulesByTheaterIdAndMovieIdAndNowParam param);
}
