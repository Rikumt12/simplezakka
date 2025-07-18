package com.example.simplezakka.controller;
 
import com.example.simplezakka.config.SecurityConfiguration;
import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import com.example.simplezakka.service.EmailService;
import com.example.simplezakka.service.PasswordResetTokenService;
 
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
 
import java.util.Optional;
 
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
 
@WebMvcTest(ForgotPasswordController.class)
@Import(SecurityConfiguration.class)// セキュリティ設定を読み込む
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false) // Securityフィルター無効化
class ForgotPasswordControllerTest {
 
    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private AdminRepository adminRepository;
 
    @MockBean
    private EmailService emailService;
 
    @MockBean
    private PasswordResetTokenService tokenService;
 
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
 
    @Test
    void testSendResetLink_EmailMissing() throws Exception {
        mockMvc.perform(post("/admin/api/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("メールアドレスが必要です"));
    }
 
    @Test
    @DisplayName("sendResetLink: 登録されていないメールアドレスで成功メッセージ")
    void testSendResetLink_EmailNotFound() throws Exception {
        when(adminRepository.findByEmail(anyString())).thenReturn(Optional.empty());
 
        mockMvc.perform(post("/admin/api/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"notfound@example.com\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("メールを送信しました"));
 
        verify(emailService, never()).sendResetLink(anyString(), anyString());
    }
 
    @Test
    @DisplayName("sendResetLink: 登録済みメールアドレスでリンク送信成功")
    void testSendResetLink_EmailFound() throws Exception {
        Admin admin = new Admin();
        admin.setEmail("user@example.com");
 
        when(adminRepository.findByEmail("user@example.com")).thenReturn(Optional.of(admin));
        when(tokenService.createToken("user@example.com")).thenReturn("valid-token");
        doNothing().when(emailService).sendResetLink(eq("user@example.com"), anyString());
 
        mockMvc.perform(post("/admin/api/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("パスワード再設定リンクを送信しました"));
 
        verify(emailService, times(1)).sendResetLink(eq("user@example.com"), contains("valid-token"));
    }
 
    @Test
    @DisplayName("sendResetLink: サービス例外発生で500エラー")
    void testSendResetLink_ServiceException() throws Exception {
        when(adminRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new Admin()));
        when(tokenService.createToken(anyString())).thenThrow(new RuntimeException("Token service error"));
 
        mockMvc.perform(post("/admin/api/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\"}"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("サーバーエラーが発生しました"));
    }
 
    @Test
    @DisplayName("resetPassword: トークン未入力で400エラー")
    void testResetPassword_TokenMissing() throws Exception {
        mockMvc.perform(post("/admin/api/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"newpass\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("トークンが必要です"));
    }
 
    @Test
    @DisplayName("resetPassword: パスワード未入力で400エラー")
    void testResetPassword_PasswordMissing() throws Exception {
        mockMvc.perform(post("/admin/api/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"validtoken\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("新しいパスワードが必要です"));
    }
 
    @Test
    @DisplayName("resetPassword: 無効なトークンで400エラー")
    void testResetPassword_InvalidToken() throws Exception {
        when(tokenService.isValid("invalidtoken")).thenReturn(false);
 
        mockMvc.perform(post("/admin/api/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"invalidtoken\",\"password\":\"newpass\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("無効または期限切れのトークンです"));
    }
 
    @Test
    @DisplayName("resetPassword: トークンは有効だがAdminが見つからないで400エラー")
    void testResetPassword_AdminNotFound() throws Exception {
        when(tokenService.isValid("validtoken")).thenReturn(true);
        when(tokenService.getEmailFromToken("validtoken")).thenReturn("notfound@example.com");
        when(adminRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
 
        mockMvc.perform(post("/admin/api/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"validtoken\",\"password\":\"newpass\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("アカウントが見つかりません"));
    }
 
    @Test
    @DisplayName("resetPassword: 正常にパスワードを更新し成功レスポンス")
    void testResetPassword_Success() throws Exception {
        Admin admin = new Admin();
        admin.setEmail("user@example.com");
 
        when(tokenService.isValid("validtoken")).thenReturn(true);
        when(tokenService.getEmailFromToken("validtoken")).thenReturn("user@example.com");
        when(adminRepository.findByEmail("user@example.com")).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);
        doNothing().when(tokenService).invalidate("validtoken");
 
        mockMvc.perform(post("/admin/api/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"validtoken\",\"password\":\"newpass\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("パスワードを更新しました"));
 
        verify(adminRepository).save(any(Admin.class));
        verify(tokenService).invalidate("validtoken");
    }
 
    @Test
    @DisplayName("resetPassword: サービス例外発生で500エラー")
    void testResetPassword_ServiceException() throws Exception {
        when(tokenService.isValid("validtoken")).thenReturn(true);
        when(tokenService.getEmailFromToken("validtoken")).thenReturn("user@example.com");
        when(adminRepository.findByEmail("user@example.com")).thenThrow(new RuntimeException("DB error"));
 
        mockMvc.perform(post("/admin/api/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"validtoken\",\"password\":\"newpass\"}"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("サーバーエラーが発生しました"));
    }
}
 