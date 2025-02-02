package com.tdd.movie.domain.theater.service;


import com.tdd.movie.domain.movie.dto.MovieRepositoryParam.FindDistinctTheaterIdsByMovieIdParam;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetDistinctTheaterIdsByMovieId;
import com.tdd.movie.domain.theater.dto.TheaterQuery.GetTheatersByIds;
import com.tdd.movie.domain.theater.dto.TheaterRepositoryParam.FindTheatersByIds;
import com.tdd.movie.domain.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterQueryService {

    private final TheaterRepository theaterRepository;

    public List<Theater> getTheaters(GetTheatersByIds query) {
        return theaterRepository.findTheaters(
                new FindTheatersByIds(query.theaterIds())
        );
    }

    public List<Long> getDistinctTheaterIds(GetDistinctTheaterIdsByMovieId query) {
        return theaterRepository.findDistinctTheaterIds(
                new FindDistinctTheaterIdsByMovieIdParam(query.movieId())
        );
    }
}
