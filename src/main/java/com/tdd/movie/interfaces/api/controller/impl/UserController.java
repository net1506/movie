package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.application.UserFacade;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.interfaces.api.controller.IUserController;
import com.tdd.movie.interfaces.api.dto.UserControllerDto.ChargeWalletRequest;
import com.tdd.movie.interfaces.api.dto.UserControllerDto.GetWalletResponse;
import com.tdd.movie.interfaces.api.dto.UserControllerDto.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements IUserController {

    private final UserFacade userFacade;

    @Override
    @GetMapping("/{userId}/wallet")
    public ResponseEntity<GetWalletResponse> getWallet(
            @PathVariable Long userId
    ) {
        Wallet fetchedWallet = userFacade.getWallet(userId);

        WalletResponse response = new WalletResponse(fetchedWallet);

        return ResponseEntity.ok(new GetWalletResponse(response));
    }

    @Override
    @PutMapping("/{userId}/wallets/{walletId}/charge")
    public ResponseEntity<GetWalletResponse> chargeWallet(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestBody ChargeWalletRequest request
    ) {
        Wallet wallet = userFacade.chargeUserWalletAmount(userId, walletId, request.amount());

        WalletResponse response = new WalletResponse(wallet);

        return ResponseEntity.ok(new GetWalletResponse(response));
    }

}
