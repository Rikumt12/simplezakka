package com.example.simplezakka.config;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import com.example.simplezakka.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Administrator implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AdminService adminService;
    
    @Override
    public void run(String... args) throws Exception {
        // 初期管理者アカウントの作成
        try {
            adminService.createAdmin("admin", "admin123", "admin@example.com", "管理者");
            System.out.println("初期管理者アカウントを作成しました:");
            System.out.println("ユーザー名: admin");
            System.out.println("パスワード: admin123");
        } catch (IllegalArgumentException e) {
            // 既に存在する場合は何もしない
            System.out.println("管理者アカウントは既に存在します");
        }
        
        // 既存のサンプルデータロード処理があればここに残す
        // 例：
        // if (productRepository.count() == 0) {
        //     // サンプル商品データの作成
        // }
    }
}