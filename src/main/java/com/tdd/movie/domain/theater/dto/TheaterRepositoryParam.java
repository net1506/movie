package com.tdd.movie.domain.theater.dto;

import java.util.List;

public class TheaterRepositoryParam {

    public record FindTheatersByIdsParam(
            List<Long> theaterIds
    ) {

    }

}
