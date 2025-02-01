package com.tdd.movie.application;

import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.infra.db.user.UserJpaRepository;
import com.tdd.movie.infra.db.user.WalletJpaRepository;
import org.assertj.core.api.Assertions;
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

    @Test
    @DisplayName("사용자 지갑 충전 성공")
    public void shouldChargeUserWalletAmount() throws Exception {
        // given
        User savedUser = userJpaRepository.save(User.builder().name("user -").build());
        Wallet savedWallet = walletJpaRepository.save(Wallet.builder().userId(savedUser.getId()).amount(0).build());
        int amount = 1000;

        // when
        Wallet wallet = userFacade.chargeUserWalletAmount(savedUser.getId(), savedWallet.getId(), amount);

        // then
        Assertions.assertThat(wallet).isNotNull();
        assertThat(wallet.getUserId()).isEqualTo(savedUser.getId());
        assertThat(wallet.getAmount()).isEqualTo(amount);
    }
}