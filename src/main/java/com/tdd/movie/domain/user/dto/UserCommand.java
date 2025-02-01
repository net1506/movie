package com.tdd.movie.domain.user.dto;

import com.tdd.movie.domain.support.error.CoreException;

import static com.tdd.movie.domain.support.error.ErrorType.User.*;

public class UserCommand {

    public record ChargeUserWalletAmountByWalletIdCommand(
            Long walletId,
            Integer amount
    ) {
        public ChargeUserWalletAmountByWalletIdCommand {
            if (walletId == null) {
                throw new CoreException(WALLET_ID_MUST_NOT_BE_NULL);
            }

            if (amount == null) {
                throw new CoreException(AMOUNT_MUST_NOT_BE_NULL);
            }

            if (amount <= 0) {
                throw new CoreException(AMOUNT_MUST_BE_POSITIVE);
            }
        }
    }
}
