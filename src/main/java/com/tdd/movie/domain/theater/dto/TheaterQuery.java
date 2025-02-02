package com.tdd.movie.domain.theater.dto;

import java.util.List;

public class TheaterQuery {

    public record GetTheatersByIds(
            List<Long> theaterIds
    ) {

    }

    public record GetDistinctTheaterIdsByMovieId(
            Long movieId
    ) {

    }

}
