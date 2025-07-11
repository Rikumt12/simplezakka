package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import com.example.simplezakka.service.EmailService;
import com.example.simplezakka.service.PasswordResetTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/api")
public class ForgotPasswordController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenService tokenService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // パスワードリセットリンクのメール送信
    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetLink(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isEmpty()) {
            // メールが存在しない場合でも、情報漏洩を防ぐため成功を返す
            return ResponseEntity.ok(Map.of("message", "メールを送信しました"));
        }

        String token = tokenService.createToken(email);
        String resetLink = "http://localhost:8080/admin/reset-password.html?token=" + token;

        emailService.sendResetLink(email, resetLink);

        return ResponseEntity.ok(Map.of("message", "パスワード再設定リンクを送信しました"));
    }

    // パスワード更新処理
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("password");

        if (!tokenService.isValid(token)) {
            return ResponseEntity.badRequest().body(Map.of("message", "無効または期限切れのトークンです"));
        }

        String email = tokenService.getEmailFromToken(token);
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "アカウントが見つかりません"));
        }

        Admin admin = adminOpt.get();
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
        tokenService.invalidate(token); // 一度使ったトークンは無効化

        return ResponseEntity.ok(Map.of("message", "パスワードを更新しました"));
    }
}
