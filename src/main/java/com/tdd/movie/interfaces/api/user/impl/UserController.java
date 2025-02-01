package com.tdd.movie.interfaces.api.user.impl;

import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByUserIdQuery;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.service.UserQueryService;
import com.tdd.movie.interfaces.api.dto.UserControllerDto.GetWalletResponse;
import com.tdd.movie.interfaces.api.dto.UserControllerDto.WalletResponse;
import com.tdd.movie.interfaces.api.user.IUserController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements IUserController {

    private final UserQueryService userQueryService;

    @Override
    @GetMapping("/{userId}/wallet")
    public ResponseEntity<GetWalletResponse> getWallet(
            @PathVariable Long userId
    ) {
        Wallet fetchedWallet = userQueryService.getWallet(new GetUserWalletByUserIdQuery(userId));

        WalletResponse response = new WalletResponse(fetchedWallet);

        return ResponseEntity.ok(new GetWalletResponse(response));
    }

}
