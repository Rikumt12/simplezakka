package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.service.AdminService;
import com.example.simplezakka.service.ProductService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class AdminController {

    @Autowired
    private AdminService adminService;


    @Autowired
    private ProductService productService;

    @GetMapping("/login")
    public String showLogin(HttpSession session, Model model) {
        if (session.getAttribute("admin") != null) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "管理者IDとパスワードを入力してください"
                ));
            }

            Admin admin = adminService.authenticate(username.trim(), password);
            if (admin != null) {
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
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "ログイン処理中にエラーが発生しました"
            ));
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

       
        model.addAttribute("admin", admin);

        
        List<ProductListItem> products = productService.findAllProducts();
        System.out.println("管理画面の商品件数: " + products.size());
        products.forEach(p -> System.out.println(p.getProductId() + " : " + p.getName()));
        model.addAttribute("products", products);

        
        return "admin/dashboard";
    }

    @GetMapping("admin/forgot-password") 
    public String showForgotPassword() {
        return "admin/forgot-password";
    }

    @GetMapping("admin/reset-password")
    public String showResetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "admin/reset-password";
    }   

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
    
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
