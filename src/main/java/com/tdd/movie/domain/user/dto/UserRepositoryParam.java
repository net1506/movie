package com.tdd.movie.domain.user.dto;

public class UserRepositoryParam {

    public record GetUserWalletByIdParam(
            Long id
    ) {

    }

    public record GetUserWalletByUserIdParam(
            Long userId
    ) {

    }

    public record GetUserByIdParam(
            Long id
    ) {

    }
}
