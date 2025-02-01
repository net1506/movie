package com.tdd.movie.domain.user.dto;

import com.tdd.movie.domain.support.error.CoreException;

import static com.tdd.movie.domain.support.error.ErrorType.User.USER_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.User.WALLET_ID_MUST_NOT_BE_NULL;

public class UserQuery {

    public record GetUserWalletByIdQuery(
            Long id
    ) {
        public GetUserWalletByIdQuery {
            if (id == null) {
                throw new CoreException(WALLET_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record GetWalletByUserIdQuery(
            Long userId
    ) {
        public GetWalletByUserIdQuery {
            if (userId == null) {
                throw new CoreException(USER_ID_MUST_NOT_BE_NULL);
            }
        }
    }

    public record GetUserByIdQuery(
            Long id
    ) {
        public GetUserByIdQuery {
            if (id == null) {
                throw new CoreException(USER_ID_MUST_NOT_BE_NULL);
            }
        }
    }

}
