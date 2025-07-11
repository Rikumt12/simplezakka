package com.example.simplezakka.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetTokenService {

    // トークンを保存するMap（token → TokenInfo）
    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    // 有効期限（例：15分）
    private final int EXPIRATION_MINUTES = 15;

    // トークンを生成し、emailと紐付けて保存
    public String createToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        tokenStore.put(token, new TokenInfo(email, expiry));
        return token;
    }

    // トークンが有効か確認
    public boolean isValid(String token) {
        TokenInfo info = tokenStore.get(token);
        if (info == null) return false;
        return LocalDateTime.now().isBefore(info.expiry);
    }

    // トークンからメールアドレスを取得
    public String getEmailFromToken(String token) {
        TokenInfo info = tokenStore.get(token);
        if (info == null) return null;
        return info.email;
    }

    // 一度使ったトークンは削除（無効化）
    public void invalidate(String token) {
        tokenStore.remove(token);
    }

    // 内部クラス：トークン情報を保持
    private static class TokenInfo {
        String email;
        LocalDateTime expiry;

        TokenInfo(String email, LocalDateTime expiry) {
            this.email = email;
            this.expiry = expiry;
        }
    }
}
