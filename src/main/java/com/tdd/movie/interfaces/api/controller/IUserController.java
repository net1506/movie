package com.tdd.movie.interfaces.api.controller;

import com.tdd.movie.interfaces.api.dto.UserControllerDto.ChargeWalletRequest;
import com.tdd.movie.interfaces.api.dto.UserControllerDto.GetWalletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "사용자 API")
public interface IUserController {

    @Operation(summary = "지갑 조회", description = "사용자의 지갑을 조회합니다.")
    ResponseEntity<GetWalletResponse> getWallet(
            @Schema(description = "사용자 ID", example = "1")
            Long userId
    );

    @Operation(summary = "지갑 충전", description = "사용자의 지갑을 충전합니다.")
    ResponseEntity<GetWalletResponse> chargeWallet(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,
            @Schema(description = "지갑 ID", example = "1")
            Long walletId,
            @Schema(description = "잔액", example = "100000")
            ChargeWalletRequest request
    );

}
