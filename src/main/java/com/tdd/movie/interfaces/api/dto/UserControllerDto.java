package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.user.model.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class UserControllerDto {

    public record GetWalletResponse(
            WalletResponse response
    ) {

    }

    public record ChargeWalletRequest(
            Integer amount
    ) {

    }

    public record WalletResponse(
            @Schema(description = "지갑 ID", example = "1")
            Long id,

            @Schema(description = "지갑 사용자 ID", example = "1")
            Long userId,

            @Schema(description = "잔액", example = "1000")
            Integer amount,

            @Schema(description = "생성일", example = "2024-12-31T23:59:59")
            LocalDateTime createdAt,

            @Schema(description = "변경일", example = "2024-12-31T23:59:59")
            LocalDateTime updatedAt
    ) {
        public WalletResponse(Wallet wallet) {
            this(wallet.getId(), wallet.getUserId(), wallet.getAmount(), wallet.getCreatedAt(),
                    wallet.getUpdatedAt());
        }
    }
}
