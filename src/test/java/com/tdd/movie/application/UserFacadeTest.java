package com.tdd.movie.application;

import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.infra.db.user.UserJpaRepository;
import com.tdd.movie.infra.db.user.WalletJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("UserFacade 단위 테스트")
class UserFacadeTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private WalletJpaRepository walletJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    public void setUp() {
        walletJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        IntStream.range(0, 10)
                .mapToObj(i -> userJpaRepository.save(User.builder()
                        .id((long) (i + 1))
                        .name("user -" + i + 1)
                        .build()))
                .toList();

        Long userId = 99L;
        userJpaRepository.save(User.builder().id(userId).name("user -" + 99).build());
        walletJpaRepository.save(Wallet.builder().id(1L).userId(userId).amount(1000).build());
    }

    @Test
    @DisplayName("사용자 지갑 조회 성공")
    public void shouldGetWallet() throws Exception {
        // given
        Long userId = 99L;

        // when
        Wallet wallet = userFacade.getWallet(userId);

        // then
        assertThat(wallet).isNotNull();
        assertThat(wallet.getUserId()).isEqualTo(userId);
    }
}