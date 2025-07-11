package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import com.example.simplezakka.service.EmailService;
import com.example.simplezakka.service.PasswordResetTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/api")
@CrossOrigin(origins = "*") 
public class ForgotPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenService tokenService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetLink(@RequestBody Map<String, String> payload) {
        logger.info("パスワード再発行リクエストを受信: {}", payload);
        
        try {
            String email = payload.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "メールアドレスが必要です"));
            }

            Optional<Admin> adminOpt = adminRepository.findByEmail(email);
            if (adminOpt.isEmpty()) {
                logger.warn("存在しないメールアドレス: {}", email);
                return ResponseEntity.ok(Map.of("message", "メールを送信しました"));
            }

            String token = tokenService.createToken(email);
            String resetLink = "http://localhost:8080/admin/reset-password.html?token=" + token;

            emailService.sendResetLink(email, resetLink);
            logger.info("パスワード再設定リンクを送信: {}", email);

            return ResponseEntity.ok(Map.of("message", "パスワード再設定リンクを送信しました"));
            
        } catch (Exception e) {
            logger.error("パスワード再発行処理でエラー発生", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "サーバーエラーが発生しました"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        logger.info("パスワードリセットリクエストを受信");
        
        try {
            String token = payload.get("token");
            String newPassword = payload.get("password");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "トークンが必要です"));
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "新しいパスワードが必要です"));
            }

            if (!tokenService.isValid(token)) {
                logger.warn("無効なトークン: {}", token);
                return ResponseEntity.badRequest().body(Map.of("message", "無効または期限切れのトークンです"));
            }

            String email = tokenService.getEmailFromToken(token);
            Optional<Admin> adminOpt = adminRepository.findByEmail(email);
            if (adminOpt.isEmpty()) {
                logger.warn("トークンに対応するアカウントが見つからない: {}", email);
                return ResponseEntity.badRequest().body(Map.of("message", "アカウントが見つかりません"));
            }

            Admin admin = adminOpt.get();
            admin.setPassword(passwordEncoder.encode(newPassword));
            adminRepository.save(admin);
            tokenService.invalidate(token);
            
            logger.info("パスワードを更新: {}", email);
            return ResponseEntity.ok(Map.of("message", "パスワードを更新しました"));
            
        } catch (Exception e) {
            logger.error("パスワードリセット処理でエラー発生", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "サーバーエラーが発生しました"));
        }
    }
}