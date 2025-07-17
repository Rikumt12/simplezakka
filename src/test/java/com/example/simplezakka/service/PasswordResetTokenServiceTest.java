package com.example.simplezakka.service;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

class PasswordResetTokenServiceTest {

    private PasswordResetTokenService service;

    @BeforeEach
    void setup() {
        service = new PasswordResetTokenService();
    }

    @Test
    @DisplayName("createToken はUUID形式のトークンを生成し、ストアに保存する")
    void createToken_ShouldGenerateTokenAndStoreWithCorrectExpiry() {
        String email = "user@example.com";

        String token = service.createToken(email);

        assertThat(token).isNotNull();
        assertThat(token).matches("^[0-9a-fA-F\\-]{36}$"); // UUID形式
        // 内部の tokenStore に正しく保存されているかは直接は見れないが、後続のisValidで検証可能
        assertThat(service.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("isValid は有効なトークンで true を返す")
    void isValid_WithValidToken_ShouldReturnTrue() {
        String email = "user@example.com";
        String token = service.createToken(email);

        boolean valid = service.isValid(token);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isValid は存在しないトークンで false を返す")
    void isValid_WithInvalidToken_ShouldReturnFalse() {
        boolean valid = service.isValid("invalid-token");

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("isValid は期限切れトークンで false を返す")
    void isValid_WithExpiredToken_ShouldReturnFalse() throws Exception {
        String email = "user@example.com";
        String token = service.createToken(email);

        // トークンの有効期限を強制的に過去に設定（リフレクションでprivateフィールドアクセス）
        var tokenStoreField = PasswordResetTokenService.class.getDeclaredField("tokenStore");
        tokenStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenStore = (Map<String, Object>) tokenStoreField.get(service);

        // TokenInfoのexpiryを過去にするため、TokenInfoクラスのフィールドにアクセス
        var tokenInfo = tokenStore.get(token);
        var tokenInfoClass = tokenInfo.getClass();

        var expiryField = tokenInfoClass.getDeclaredField("expiry");
        expiryField.setAccessible(true);
        expiryField.set(tokenInfo, LocalDateTime.now().minusMinutes(1));

        boolean valid = service.isValid(token);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("getEmailFromToken は有効なトークンで対応するメールアドレスを返す")
    void getEmailFromToken_WithValidToken_ShouldReturnEmail() {
        String email = "user@example.com";
        String token = service.createToken(email);

        String extractedEmail = service.getEmailFromToken(token);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("getEmailFromToken は無効なトークンで null を返す")
    void getEmailFromToken_WithInvalidToken_ShouldReturnNull() {
        String email = service.getEmailFromToken("invalid-token");

        assertThat(email).isNull();
    }

    @Test
    @DisplayName("invalidate はトークンを削除し、その後は isValid が false になる")
    void invalidate_ShouldRemoveTokenFromStore() {
        String email = "user@example.com";
        String token = service.createToken(email);

        assertThat(service.isValid(token)).isTrue();

        service.invalidate(token);

        assertThat(service.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("invalidate は存在しないトークンでも例外を投げずに処理される")
    void invalidate_WithNonExistingToken_ShouldNotThrow() {
        assertThatCode(() -> service.invalidate("non-existing-token")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("isValid は有効期限ちょうどの時点で false を返す（境界値テスト）")
    void isValid_WhenTokenJustExpired_ShouldReturnFalse() throws Exception {
        String email = "user@example.com";
        String token = service.createToken(email);

        var tokenStoreField = PasswordResetTokenService.class.getDeclaredField("tokenStore");
        tokenStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenStore = (Map<String, Object>) tokenStoreField.get(service);

        var tokenInfo = tokenStore.get(token);
        var tokenInfoClass = tokenInfo.getClass();

        var expiryField = tokenInfoClass.getDeclaredField("expiry");
        expiryField.setAccessible(true);
        expiryField.set(tokenInfo, LocalDateTime.now());

        boolean valid = service.isValid(token);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("複数トークンが正しく管理され、各トークンで正しいメールが取得できる")
    void getEmailFromToken_WithMultipleTokens_ShouldReturnCorrectEmail() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        String token1 = service.createToken(email1);
        String token2 = service.createToken(email2);

        assertThat(service.getEmailFromToken(token1)).isEqualTo(email1);
        assertThat(service.getEmailFromToken(token2)).isEqualTo(email2);

        assertThat(token1).isNotEqualTo(token2);
    }
}
