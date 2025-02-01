package com.tdd.movie.domain.user.service;

import com.tdd.movie.domain.user.dto.UserCommand.ChargeUserWalletAmountByWalletIdCommand;
import com.tdd.movie.domain.user.dto.UserRepositoryParam.GetUserWalletByIdParam;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {

    private final UserRepository userRepository;

    public void chargeUserWalletAmount(ChargeUserWalletAmountByWalletIdCommand command) {
        Wallet wallet = userRepository.getWallet(new GetUserWalletByIdParam(command.walletId()));

        wallet.chargeAmount(command.amount());

        userRepository.saveWallet(wallet);
    }

}
