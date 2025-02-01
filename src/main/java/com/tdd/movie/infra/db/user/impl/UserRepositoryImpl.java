package com.tdd.movie.infra.db.user.impl;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserByIdParam;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByIdParam;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByUserIdParam;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.repository.UserRepository;
import com.tdd.movie.infra.db.user.UserJpaRepository;
import com.tdd.movie.infra.db.user.WalletJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.tdd.movie.domain.support.error.ErrorType.User.USER_NOT_FOUND;
import static com.tdd.movie.domain.support.error.ErrorType.User.WALLET_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final WalletJpaRepository walletJpaRepository;

    private final UserJpaRepository userJpaRepository;

    @Override
    public Wallet saveWallet(Wallet wallet) {
        return walletJpaRepository.save(wallet);
    }

    @Override
    public Wallet getWallet(GetUserWalletByUserIdParam param) {
        return walletJpaRepository.findByUserId(param.userId())
                .orElseThrow(() -> new CoreException(WALLET_NOT_FOUND));
    }

    @Override
    public Wallet getWallet(GetUserWalletByIdParam param) {
        return walletJpaRepository.findById(param.id())
                .orElseThrow(() -> new CoreException(WALLET_NOT_FOUND));
    }

    @Override
    public User getUser(GetUserByIdParam param) {
        return userJpaRepository.findById(param.id())
                .orElseThrow(() -> new CoreException(USER_NOT_FOUND));
    }
}
