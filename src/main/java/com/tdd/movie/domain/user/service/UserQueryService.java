package com.tdd.movie.domain.user.service;

import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByUserIdQuery;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByIdParam;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByUserIdParam;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public Wallet getWallet(GetUserWalletByIdQuery query) {
        return userRepository.getWallet(new GetUserWalletByIdParam(query.id()));
    }

    public Wallet getWallet(GetUserWalletByUserIdQuery query) {
        return userRepository.getWallet(new GetUserWalletByUserIdParam(query.userId()));
    }
}
