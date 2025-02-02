package com.tdd.movie.domain.movie.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
@NoArgsConstructor
@Getter
public class Movie extends BaseEntity {

    private String title;

    private String plot;

    private String posterImageUrl;

    private Integer runningTime;

    private LocalDate screeningStartDate;

    private LocalDate screeningEndDate;

    @Builder
    public Movie(Long id, String title, String plot, String posterImageUrl, Integer runningTime, LocalDate screeningStartDate, LocalDate screeningEndDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.title = title;
        this.plot = plot;
        this.posterImageUrl = posterImageUrl;
        this.runningTime = runningTime;
        this.screeningStartDate = screeningStartDate;
        this.screeningEndDate = screeningEndDate;
    }
}
