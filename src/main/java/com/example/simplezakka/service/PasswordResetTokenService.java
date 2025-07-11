package com.example.simplezakka.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetTokenService {

    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    private final int EXPIRATION_MINUTES = 15;

    public String createToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        tokenStore.put(token, new TokenInfo(email, expiry));
        return token;
    }

    public boolean isValid(String token) {
        TokenInfo info = tokenStore.get(token);
        if (info == null) return false;
        return LocalDateTime.now().isBefore(info.expiry);
    }

    public String getEmailFromToken(String token) {
        TokenInfo info = tokenStore.get(token);
        if (info == null) return null;
        return info.email;
    }

    public void invalidate(String token) {
        tokenStore.remove(token);
    }

    private static class TokenInfo {
        String email;
        LocalDateTime expiry;

        TokenInfo(String email, LocalDateTime expiry) {
            this.email = email;
            this.expiry = expiry;
        }
    }
}
