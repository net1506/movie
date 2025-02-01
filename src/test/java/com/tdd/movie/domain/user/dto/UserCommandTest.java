package com.tdd.movie.domain.user.dto;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.user.dto.UserCommand.ChargeUserWalletAmountByWalletIdCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tdd.movie.domain.support.error.ErrorType.User.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserCommand 단위 테스트")
class UserCommandTest {

    @Nested
    @DisplayName("ChargeUserWalletAmountByIdCommand 생성자 테스트")
    class ChargeUserWalletAmountByWalletIdCommandTest {
        @Test
        @DisplayName("생성자 생성 실패 - Wallet Id 가 NULL 인 경우")
        public void shouldThrowExceptionWhenIdIsNull() throws Exception {
            // given
            Long id = null;
            Integer amount = 1000;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new ChargeUserWalletAmountByWalletIdCommand(id, amount));

            // then
            assertThat(coreException.getMessage()).isEqualTo(WALLET_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 실패 - Amount 가 NULL 인 경우")
        public void shouldThrowExceptionWhenAmountIsNull() throws Exception {
            // given
            Long id = 1L;
            Integer amount = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new ChargeUserWalletAmountByWalletIdCommand(id, amount));

            // then
            assertThat(coreException.getMessage()).isEqualTo(AMOUNT_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 실패 - Id 가 NULL 인 음수")
        public void shouldThrowExceptionWhenAmountIsNegative() throws Exception {
            // given
            Long id = 1L;
            Integer amount = -1;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new ChargeUserWalletAmountByWalletIdCommand(id, amount));

            // then
            assertThat(coreException.getMessage()).isEqualTo(AMOUNT_MUST_BE_POSITIVE.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldChargeUserWalletAmountByWalletIdCommand() throws Exception {
            // given
            Long walletId = 1L;
            Integer amount = 1000;

            // when
            ChargeUserWalletAmountByWalletIdCommand getUserWalletByIdQuery = new ChargeUserWalletAmountByWalletIdCommand(walletId, amount);

            // then
            assertThat(getUserWalletByIdQuery.walletId()).isEqualTo(walletId);
            assertThat(getUserWalletByIdQuery.amount()).isEqualTo(amount);
        }
    }

}