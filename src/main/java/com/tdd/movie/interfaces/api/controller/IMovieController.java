package com.tdd.movie.interfaces.api.controller;

import com.tdd.movie.interfaces.api.dto.MovieControllerDto.GetAvailableTheatersResponse;
import com.tdd.movie.interfaces.api.dto.MovieControllerDto.GetComingSoonMoviesResponse;
import com.tdd.movie.interfaces.api.dto.MovieControllerDto.GetMovieResponse;
import com.tdd.movie.interfaces.api.dto.MovieControllerDto.GetNowShowingMoviesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Movie", description = "영화 API")
public interface IMovieController {

    @Operation(summary = "영화 조회", description = "영화를 조회합니다.")
    ResponseEntity<GetMovieResponse> getMovie(Long movieId);

    @Operation(summary = "상영 중인 영화 조회", description = "상영중인 영화 목록을 조회합니다.")
    ResponseEntity<GetNowShowingMoviesResponse> getNowShowingMovies();

    @Operation(summary = "상영 예정 영화 조회", description = "상영예정 영화 목록을 조회합니다.")
    ResponseEntity<GetComingSoonMoviesResponse> getComingSoonMovies();

    @Operation(summary = "상영 가능한 영화관 조회", description = "상영 가능한 영화관 목록을 조회합니다.")
    ResponseEntity<GetAvailableTheatersResponse> getAvailableTheaters(
            @Schema(description = "영화 ID", example = "1")
            Long movieId
    );

}
