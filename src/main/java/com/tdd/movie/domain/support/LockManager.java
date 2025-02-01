package com.tdd.movie.domain.support;

import java.util.function.Supplier;

public interface LockManager {

    Object lock(String lockName, Supplier<Object> operation) throws Throwable;

}