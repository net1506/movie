package com.tdd.movie.domain.support;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DistributedLockType {
    USER_WALLET("userWalletLock");

    private final String lockName;

    public String lockName() {
        return lockName;
    }
}