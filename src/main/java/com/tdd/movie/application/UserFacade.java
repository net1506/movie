package com.tdd.movie.application;

import com.tdd.movie.domain.user.dto.UserCommand.ChargeUserWalletAmountByWalletIdCommand;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetWalletByUserIdQuery;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.service.UserCommandService;
import com.tdd.movie.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserQueryService userQueryService;

    private final UserCommandService userCommandService;

    public Wallet getWallet(Long userId) {
        return userQueryService.getWallet(
                new GetWalletByUserIdQuery(userId)
        );
    }

    public Wallet chargeUserWalletAmount(Long userId, Long walletId, Integer amount) {
        // 사용자 조회
        User user = userQueryService.getUser(new GetUserByIdQuery(userId));

        // 지갑 조회
        Wallet wallet = userQueryService.getWallet(new GetUserWalletByIdQuery(walletId));

        // 지갑의 소유자를 확인한다.
        wallet.validateWalletOwner(user.getId());

        // 지갑 충전
        userCommandService.chargeUserWalletAmount(new ChargeUserWalletAmountByWalletIdCommand(wallet.getId(), amount));

        // 지갑 정보 불러오기
        return userQueryService.getWallet(new GetWalletByUserIdQuery(userId));
    }
}
