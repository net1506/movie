package com.tdd.movie.application;

import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByUserIdQuery;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserQueryService userQueryService;

    public Wallet getWallet(Long userId) {
        return userQueryService.getWallet(
                new GetUserWalletByUserIdQuery(userId)
        );
    }
}
