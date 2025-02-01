package com.tdd.movie.domain.user.service;

import com.tdd.movie.domain.user.dto.UserQuery.GetUserByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetWalletByUserIdQuery;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserByIdParam;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByIdParam;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByUserIdParam;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    public Wallet getWallet(GetUserWalletByIdQuery query) {
        return userRepository.getWallet(new GetUserWalletByIdParam(query.id()));
    }

    public Wallet getWallet(GetWalletByUserIdQuery query) {
        return userRepository.getWallet(new GetUserWalletByUserIdParam(query.userId()));
    }

    public User getUser(GetUserByIdQuery query) {
        return userRepository.getUser(new GetUserByIdParam(query.id()));
    }
}
