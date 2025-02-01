package com.tdd.movie.domain.user.model;

import com.tdd.movie.domain.common.base.BaseEntity;
import com.tdd.movie.domain.support.error.CoreException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.tdd.movie.domain.support.error.ErrorType.User.*;
import static com.tdd.movie.domain.user.UserConstants.MAX_WALLET_AMOUNT;

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

    public void validateWalletOwner(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new CoreException(WALLET_NOT_MATCH_USER);
        }
    }

    public void chargeAmount(Integer chargedAmount) {
        // 충전 금액
        int addedAmount = this.amount + chargedAmount;

        if (chargedAmount == null || chargedAmount <= 0) {
            throw new CoreException(INVALID_AMOUNT);
        }

        if (addedAmount > MAX_WALLET_AMOUNT) {
            throw new CoreException(EXCEED_LIMIT_AMOUNT);
        }

        // 충전 금액
        this.amount = addedAmount;
    }
}
