package com.tdd.movie.domain.user.repository;

import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByIdParam;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByUserIdParam;
import com.tdd.movie.domain.user.model.Wallet;

public interface UserRepository {
    Wallet getWallet(GetUserWalletByUserIdParam param);

    Wallet getWallet(GetUserWalletByIdParam param);
}
