package com.example.simplezakka.service;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
 
    @PostConstruct
    public void initializeDefaultAdmin() {
        String defaultUsername = "admin";
        String defaultPassword = "admin123";
        String defaultName = "システム管理者";
        String defaultEmail = "admin@simplezakka.com";
        
        try {
            Optional<Admin> existingAdmin = adminRepository.findByUsername(defaultUsername);
            
            if (existingAdmin.isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername(defaultUsername);
                admin.setPassword(passwordEncoder.encode(defaultPassword));
                admin.setName(defaultName);
                admin.setEmail(defaultEmail);
                admin.setRole("ADMIN");
                admin.setActive(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                
                adminRepository.save(admin);
                System.out.println("初期管理者アカウントを作成しました:");
                System.out.println("  管理者ID: " + defaultUsername);
                System.out.println("  パスワード: " + defaultPassword);
                System.out.println("  ※本番環境では必ずパスワードを変更してください");
            } else {
                System.out.println("管理者アカウントは既に存在します");
            }
        } catch (Exception e) {
            System.err.println("初期管理者アカウントの作成に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

  
    public void createAdmin(String username, String password, String name, String email) {
        if (adminRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("すでに存在するユーザー名です: " + username);
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setName(name);
        admin.setEmail(email);
        admin.setRole("ADMIN");
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        adminRepository.save(admin);
    }

 
    public Admin authenticate(String username, String password) {
        try {
            Optional<Admin> adminOpt = adminRepository.findByUsername(username);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                if (!admin.isActive()) {
                    System.out.println("管理者アカウントが無効です: " + username);
                    return null;
                }
                if (passwordEncoder.matches(password, admin.getPassword())) {
                    admin.setLastLoginAt(LocalDateTime.now());
                    adminRepository.save(admin);
                    System.out.println("管理者ログイン成功: " + username);
                    return admin;
                } else {
                    System.out.println("パスワードが間違っています: " + username);
                }
            } else {
                System.out.println("管理者が見つかりません: " + username);
            }
            return null;
        } catch (Exception e) {
            System.err.println("認証処理でエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

 
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }

    public Admin save(Admin admin) {
        admin.setUpdatedAt(LocalDateTime.now());
        return adminRepository.save(admin);
    }

    public Admin changePassword(Admin admin, String newPassword) {
        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setUpdatedAt(LocalDateTime.now());
        return adminRepository.save(admin);
    }
}
