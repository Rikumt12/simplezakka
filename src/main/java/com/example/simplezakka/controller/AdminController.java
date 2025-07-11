package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}) // CORS設定追加
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * ログイン画面表示
     */
    @GetMapping("/login")
    public String showLogin(HttpSession session, Model model) {
        // 既にログイン済みの場合はダッシュボードにリダイレクト
        if (session.getAttribute("admin") != null) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }
    
    /**
     * ログイン処理API
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            
            // 入力値検証
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "管理者IDとパスワードを入力してください"
                ));
            }
            
            // 認証処理
            Admin admin = adminService.authenticate(username.trim(), password);
            if (admin != null) {
                // セッションに管理者情報を保存
                session.setAttribute("admin", admin);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "ログインしました"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "管理者IDまたはパスワードが間違っています"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace(); // ログ出力
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "ログイン処理中にエラーが発生しました"
            ));
        }
    }
    
    /**
     * ダッシュボード表示
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("admin", admin);
        return "admin/dashboard";
    }
    
    /**
     * ログアウト処理
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
    
    /**
     * ログアウト処理API
     */
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<?> logoutApi(HttpSession session) {
        try {
            session.invalidate();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "ログアウトしました"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "ログアウト処理中にエラーが発生しました"
            ));
        }
    }
    
    /**
     * 現在のログイン状態確認API
     */
    @GetMapping("/api/status")
    @ResponseBody
    public ResponseEntity<?> getLoginStatus(HttpSession session) {
        try {
            Admin admin = (Admin) session.getAttribute("admin");
            if (admin != null) {
                return ResponseEntity.ok(Map.of(
                    "loggedIn", true,
                    "admin", Map.of(
                        "username", admin.getUsername(),
                        "name", admin.getName() != null ? admin.getName() : "",
                        "email", admin.getEmail() != null ? admin.getEmail() : ""
                    )
                ));
            } else {
                return ResponseEntity.ok(Map.of("loggedIn", false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }
    }
}