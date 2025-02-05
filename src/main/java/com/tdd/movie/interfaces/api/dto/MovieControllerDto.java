package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.movie.model.Movie;
import com.tdd.movie.domain.theater.model.Theater;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public class MovieControllerDto {

    public record GetMovieResponse(
            MovieResponse movie
    ) {

    }

    public record GetNowShowingMoviesResponse(
            List<MovieResponse> movies
    ) {

    }

    public record GetComingSoonMoviesResponse(
            List<MovieResponse> movies
    ) {

    }

    public record GetAvailableTheatersResponse(
            List<TheaterResponse> theaters
    ) {

    }

    public record MovieResponse(
            @Schema(description = "영화 ID", example = "1")
            Long id,

            @Schema(description = "영화 제목", example = "드래곤볼")
            String title,

            @Schema(description = "줄거리", example = "드래곤볼 줄거리")
            String plot,

            @Schema(description = "포스터 이미지 URL", example = "/images/backdrop/dragonball-image.png")
            String posterImageUrl,

            @Schema(description = "상영시간 (분단위)", example = "107")
            Integer runningTime,

            @Schema(description = "변경일", example = "2024-10-09")
            LocalDate screeningStartDate,

            @Schema(description = "변경일", example = "2024-10-29")
            LocalDate screeningEndDate
    ) {
        public MovieResponse(Movie movie) {
            this(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getPlot(),
                    movie.getPosterImageUrl(),
                    movie.getRunningTime(),
                    movie.getScreeningStartDate(),
                    movie.getScreeningEndDate()
            );
        }
    }

    public record TheaterResponse(
            @Schema(description = "극장 ID", example = "1")
            Long id,

            @Schema(description = "극장 이름", example = "CGV 강남")
            String name,

            @Schema(description = "극장 주소", example = "서울특별시 강남구 강남대로 102길 23")
            String address,

            @Schema(description = "극장 이미지 URL", example = "/images/theaters/cgv-gangnam.png")
            String img,

            @Schema(description = "위도", example = "37.498095")
            String x,

            @Schema(description = "경도", example = "127.027610")
            String y
    ) {
        public TheaterResponse(Theater theater) {
            this(
                    theater.getId(),
                    theater.getName(),
                    theater.getAddress(),
                    theater.getImg(),
                    theater.getX(),
                    theater.getY()
            );
        }
    }

}
