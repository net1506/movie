package com.tdd.movie.domain.payment.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class Payment extends BaseEntity {
    private Long reservationId; // 예매 내역 번호
    private Long userId; // 결재자 아이디
    private Integer amount; // 결재 금액

    @Builder
    public Payment(Long id, Long reservationId, Long userId, Integer amount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.reservationId = reservationId;
        this.userId = userId;
        this.amount = amount;
    }
}
