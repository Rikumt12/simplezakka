package com.example.simplezakka.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

class PasswordResetTokenServiceTest {

    private PasswordResetTokenService service;

    @BeforeEach
    void setup() {
        service = new PasswordResetTokenService();
    }

    @Test
    @DisplayName("UUID形式のトークンを生成し、ストアに保存される")
    void createToken_ShouldGenerateTokenAndStoreWithCorrectExpiry() {
        String email = "user@example.com";
        String token = service.createToken(email);

        assertThat(token).isNotNull();
        assertThat(token).matches("^[0-9a-fA-F\\-]{36}$");
        assertThat(service.isValid(token)).isTrue();
    }

@Test
@DisplayName("isValid はトークンの状態に応じて true または false を返す")
void isValid_ShouldReturnExpectedResultForVariousTokens() throws Exception {
    String validToken = service.createToken("valid@example.com");
    assertThat(service.isValid(validToken)).isTrue();

    String invalidToken = "not_exist_token";
    assertThat(service.isValid(invalidToken)).isFalse();

    String expiredToken = service.createToken("expired@example.com");
    setTokenExpiry(expiredToken, LocalDateTime.now().minusMinutes(1));
    assertThat(service.isValid(expiredToken)).isFalse();
}


    @Test
    @DisplayName("有効なトークンでメールアドレスを返す")
    void getEmailFromToken_WithValidToken_ShouldReturnEmail() {
        String email = "user@example.com";
        String token = service.createToken(email);

        assertThat(service.getEmailFromToken(token)).isEqualTo(email);
    }

    @Test
    @DisplayName("無効なトークンで null を返す")
    void getEmailFromToken_WithInvalidToken_ShouldReturnNull() {
        assertThat(service.getEmailFromToken("non-existent-token")).isNull();
    }

    @Test
    @DisplayName("トークンを削除し、以降 isValid が false を返す")
    void invalidate_ShouldRemoveTokenFromStore() {
        String token = service.createToken("user@example.com");

        assertThat(service.isValid(token)).isTrue();

        service.invalidate(token);

        assertThat(service.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("存在しないトークンを無効化しても例外は発生しない")
    void invalidate_WithNonExistingToken_ShouldNotThrow() {
        assertThatCode(() -> service.invalidate("non-existent-token")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("例外時にも isValid は false を返す（境界値テスト）")
    void isValid_WhenTokenJustExpired_ShouldReturnFalse() throws Exception {
        String token = service.createToken("user@example.com");
        setTokenExpiry(token, LocalDateTime.now());

        assertThat(service.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("複数のトークンが独立して正しく管理される")
    void getEmailFromToken_WithMultipleTokens_ShouldReturnCorrectEmail() {
        String token1 = service.createToken("user1@example.com");
        String token2 = service.createToken("user2@example.com");

        assertThat(service.getEmailFromToken(token1)).isEqualTo("user1@example.com");
        assertThat(service.getEmailFromToken(token2)).isEqualTo("user2@example.com");
        assertThat(token1).isNotEqualTo(token2);
    }

    private void setTokenExpiry(String token, LocalDateTime newExpiry) throws Exception {
        Field tokenStoreField = PasswordResetTokenService.class.getDeclaredField("tokenStore");
        tokenStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenStore = (Map<String, Object>) tokenStoreField.get(service);

        Object tokenInfo = tokenStore.get(token);
        Field expiryField = tokenInfo.getClass().getDeclaredField("expiry");
        expiryField.setAccessible(true);
        expiryField.set(tokenInfo, newExpiry);
    }
}
