package com.tdd.movie.infra.db.user;

import com.tdd.movie.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
