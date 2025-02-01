package com.tdd.movie.domain.user.dto;

import com.tdd.movie.domain.support.error.CoreException;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetUserWalletByIdQuery;
import com.tdd.movie.domain.user.dto.UserQuery.GetWalletByUserIdQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tdd.movie.domain.support.error.ErrorType.User.USER_ID_MUST_NOT_BE_NULL;
import static com.tdd.movie.domain.support.error.ErrorType.User.WALLET_ID_MUST_NOT_BE_NULL;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserQuery 단위 테스트")
class UserQueryTest {

    @Nested
    @DisplayName("GetUserWalletByIdQuery 생성자 테스트")
    class GetUserWalletByIdQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - Id 가 NULL 인 경우")
        public void shouldThrowExceptionWhenIdIsNull() throws Exception {
            // given
            Long id = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new GetUserWalletByIdQuery(id));

            // then
            assertThat(coreException.getMessage()).isEqualTo(WALLET_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldGetUserWalletByIdQuery() throws Exception {
            // given
            Long id = 1L;

            // when
            GetUserWalletByIdQuery getUserWalletByIdQuery = new GetUserWalletByIdQuery(id);

            // then
            assertThat(getUserWalletByIdQuery.id()).isEqualTo(id);
        }
    }

    @Nested
    @DisplayName("GetUserWalletByUserIdQuery 생성자 테스트")
    class GetWalletByUserIdQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - userId 가 NULL 인 경우")
        public void shouldThrowExceptionWhenUserIdIsNull() throws Exception {
            // given
            Long userId = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new GetWalletByUserIdQuery(userId));

            // then
            assertThat(coreException.getMessage()).isEqualTo(USER_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldGetUserWalletByIdQuery() throws Exception {
            // given
            Long userId = 1L;

            // when
            GetWalletByUserIdQuery getWalletByUserIdQuery = new GetWalletByUserIdQuery(userId);

            // then
            assertThat(getWalletByUserIdQuery.userId()).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("GetUserByIdQuery 생성자 테스트")
    class GetUserByIdQueryTest {
        @Test
        @DisplayName("생성자 생성 실패 - id 가 NULL 인 경우")
        public void shouldThrowExceptionWhenIdIsNull() throws Exception {
            // given
            Long id = null;

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new GetUserByIdQuery(id));

            // then
            assertThat(coreException.getMessage()).isEqualTo(USER_ID_MUST_NOT_BE_NULL.getMessage());
        }

        @Test
        @DisplayName("생성자 생성 성공")
        public void shouldGetUserWalletByIdQuery() throws Exception {
            // given
            Long id = 1L;

            // when
            GetUserByIdQuery getUserByIdQuery = new GetUserByIdQuery(id);

            // then
            assertThat(getUserByIdQuery.id()).isEqualTo(id);
        }
    }
}