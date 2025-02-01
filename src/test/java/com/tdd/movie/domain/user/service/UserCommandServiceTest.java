package com.tdd.movie.domain.user.service;

import com.tdd.movie.domain.user.dto.UserCommand.ChargeUserWalletAmountByWalletIdCommand;
import com.tdd.movie.domain.user.dto.UserQuery;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.infra.db.user.UserJpaRepository;
import com.tdd.movie.infra.db.user.WalletJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("UserCommandService 단위 테스트")
class UserCommandServiceTest {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private WalletJpaRepository walletJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    public void setUp() {
        userJpaRepository.deleteAll();
        walletJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("사용자 지갑 금액 충전 테스트")
    class chargeUserWalletAmountTest {
        @Test
        @DisplayName("사용자 지갑 금액 충전 성공")
        public void shouldChargeUserWalletAmount() throws Exception {
            // given
            User savedUser = userJpaRepository.save(User.builder().name("user -").build());
            Wallet savedWallet = walletJpaRepository.save(Wallet.builder().userId(savedUser.getId()).amount(0).build());
            int amount = 1000;

            // when
            userCommandService.chargeUserWalletAmount(new ChargeUserWalletAmountByWalletIdCommand(savedWallet.getId(), amount));

            Wallet wallet = userQueryService.getWallet(new UserQuery.GetUserWalletByIdQuery(savedWallet.getId()));

            // then
            assertThat(wallet).isNotNull();
            assertThat(wallet.getUserId()).isEqualTo(savedUser.getId());
            assertThat(wallet.getAmount()).isEqualTo(amount);
        }
    }
}