package com.tdd.movie.domain.user.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class Wallet extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    private Integer amount;

    @Builder
    public Wallet(Long id, Long userId, Integer amount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.amount = amount;
    }
}
