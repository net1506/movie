package com.tdd.movie.domain.theater.domain;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "theaters")
@NoArgsConstructor
@Getter
public class Theater extends BaseEntity {

    private String name;

    private String address;

    private String img;

    private String x;

    private String y;

    @Builder
    public Theater(Long id, String name, String address, String img, String x, String y, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.address = address;
        this.img = img;
        this.x = x;
        this.y = y;
    }
}
