package com.example.simplezakka.service;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 管理者ログイン認証
     */
    public Admin authenticate(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsernameAndActive(username, true);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                return admin;
            }
        }
        return null;
    }
    
    /**
     * 管理者作成
     */
    public Admin createAdmin(String username, String password, String email, String name) {
        // 既存チェック
        if (adminRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("ユーザー名は既に使用されています");
        }
        if (adminRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("メールアドレスは既に使用されています");
        }
        
        // パスワードハッシュ化
        String hashedPassword = passwordEncoder.encode(password);
        
        Admin admin = new Admin(username, hashedPassword, email, name);
        return adminRepository.save(admin);
    }
    
    /**
     * 管理者情報取得
     */
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsernameAndActive(username, true);
    }
    
    /**
     * 管理者ID取得
     */
    public Optional<Admin> findById(Long adminId) {
        return adminRepository.findById(adminId);
    }
    
    /**
     * パスワード変更
     */
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(oldPassword, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(newPassword));
                adminRepository.save(admin);
            } else {
                throw new IllegalArgumentException("現在のパスワードが間違っています");
            }
        } else {
            throw new IllegalArgumentException("管理者が見つかりません");
        }
    }
}