package com.tdd.movie.interfaces.api.movie.impl;

import com.tdd.movie.application.MovieFacade;
import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.theater.domain.Theater;
import com.tdd.movie.interfaces.api.dto.MovieControllerDto.*;
import com.tdd.movie.interfaces.api.movie.IMovieController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController implements IMovieController {

    private final MovieFacade movieFacade;

    @Override
    @GetMapping("/now-showing")
    public ResponseEntity<GetNowShowingMoviesResponse> getNowShowingMovies() {
        // 현재 상영 중인 영화 목록 가져오기
        List<Movie> playingMovies = movieFacade.getPlayingMovies(LocalDateTime.now().toLocalDate());

        // Movie 엔티티 -> MovieResponse DTO 변환
        List<MovieResponse> movieResponses = playingMovies.stream()
                .map(MovieResponse::new)
                .toList();  // collect(Collectors.toList())

        // 응답 객체 생성
        GetNowShowingMoviesResponse response = new GetNowShowingMoviesResponse(movieResponses);

        // ResponseEntity 로 반환
        return ResponseEntity.ok(response);
    }


    @Override
    @GetMapping("/upcoming")
    public ResponseEntity<GetUpcomingMoviesResponse> getUpcomingMovies() {
        // 상영 예정 영화 목록 가져오기
        List<Movie> upcomingMovies = movieFacade.getUpcomingMovies(LocalDateTime.now().toLocalDate());

        // Movie 엔티티 -> MovieResponse DTO 변환
        List<MovieResponse> movieResponses = upcomingMovies.stream()
                .map(MovieResponse::new)
                .toList();  // collect(Collectors.toList())

        // 응답 객체 생성
        GetUpcomingMoviesResponse response = new GetUpcomingMoviesResponse(movieResponses);

        // ResponseEntity 로 반환
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{movieId}/theaters")
    public ResponseEntity<GetAvailableTheatersResponse> getAvailableTheaters(Long movieId) {
        List<Theater> screeningTheaters = movieFacade.getScreeningTheaters(movieId);

        // Movie 엔티티 -> TheaterResponse DTO 변환
        List<TheaterResponse> theaterResponses = screeningTheaters.stream()
                .map(TheaterResponse::new)
                .toList();  // collect(Collectors.toList())

        GetAvailableTheatersResponse response = new GetAvailableTheatersResponse(theaterResponses);

        // ResponseEntity 로 반환
        return ResponseEntity.ok(response);
    }
}
