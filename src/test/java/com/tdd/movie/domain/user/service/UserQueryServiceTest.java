package com.tdd.movie.domain.user.service;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.support.error.ErrorType;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByUserIdQuery;
import com.tdd.movie.domain.user.model.User;
import com.tdd.movie.domain.user.model.Wallet;
import com.tdd.movie.infra.db.user.UserJpaRepository;
import com.tdd.movie.infra.db.user.WalletJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

import static com.tdd.movie.domain.support.error.ErrorType.User.USER_ID_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("UserQueryService 단위 테스트")
class UserQueryServiceTest {

    @Autowired
    private UserQueryService userQueryService;

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
    }

    @Test
    @DisplayName("사용자 지갑 조회 테스트 실패 - userId 가 NULL 인 경우")
    public void shouldThrowExceptionWhenUserIdIsNull() throws Exception {
        // given
        Long userId = null;

        // when
        CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userQueryService.getWallet(new GetUserWalletByUserIdQuery(userId)));

        // then
        assertThat(coreException.getErrorType()).isEqualTo(USER_ID_MUST_NOT_BE_NULL);
        assertThat(coreException.getMessage()).isEqualTo(USER_ID_MUST_NOT_BE_NULL.getMessage());
    }

    @Test
    @DisplayName("사용자 지갑 조회 테스트 실패 - 사용자가 존재하지 않는 경우")
    public void shouldGetWalletCaseNotFoundWallet() throws Exception {
        // given
        Long userId = 1L;

        // when
        CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userQueryService.getWallet(new GetUserWalletByUserIdQuery(userId)));

        // then
        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.User.WALLET_NOT_FOUND);
        assertThat(coreException.getMessage()).isEqualTo(ErrorType.User.WALLET_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("사용자 지갑 조회 테스트 성공")
    public void shouldGetWallet() throws Exception {
        // given
        Long userId = 99L;
        userJpaRepository.save(User.builder().id(userId).name("user -" + 99).build());
        walletJpaRepository.save(Wallet.builder().id(1L).userId(userId).amount(1000).build());

        // when
        Wallet wallet = userQueryService.getWallet(new GetUserWalletByUserIdQuery(userId));

        // then
        assertThat(wallet).isNotNull();
        assertThat(wallet.getUserId()).isEqualTo(userId);
        assertThat(wallet.getAmount()).isEqualTo(1000);
    }

}