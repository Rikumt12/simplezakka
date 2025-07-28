package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.service.AdminService;
import com.example.simplezakka.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }


    @Test
    void showLogin_NoSession_ReturnLoginView() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @Test
    void showLogin_WithSession_RedirectDashboard() throws Exception {
        session.setAttribute("admin", new Admin());
        mockMvc.perform(get("/admin/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    void login_ValidCredentials_ReturnSuccess() throws Exception {
        Admin dummyAdmin = new Admin();
        dummyAdmin.setUsername("admin");
        dummyAdmin.setName("システム管理者");
        dummyAdmin.setEmail("admin@simplezakka.com");

        Mockito.when(adminService.authenticate("admin", "admin123"))
                .thenReturn(dummyAdmin);

        Map<String, String> requestBody = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        mockMvc.perform(post("/admin/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ログインしました"));
    }

    @Test
    void login_InvalidCredentials_ReturnError() throws Exception {
        Mockito.when(adminService.authenticate("admin", "wrongpass"))
                .thenReturn(null);

        Map<String, String> requestBody = Map.of(
                "username", "admin",
                "password", "wrongpass"
        );

        mockMvc.perform(post("/admin/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("管理者IDまたはパスワードが間違っています"));
    }

    @Test
    void login_EmptyCredentials_ReturnError() throws Exception {
        Map<String, String> requestBody = Map.of(
                "username", "",
                "password", ""
        );

        mockMvc.perform(post("/admin/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("管理者IDとパスワードを入力してください"));
    }

   @Test
void login_Exception_ReturnError() throws Exception {
    Mockito.when(adminService.authenticate(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(new RuntimeException("DB error"));

    Map<String, String> requestBody = Map.of(
            "username", "admin",
            "password", "admin123"
    );

    mockMvc.perform(post("/admin/api/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loggedIn").value(false));
}

    @Test
    void showDashboard_WithSession_ReturnDashboard() throws Exception {
        Admin admin = new Admin();
        session.setAttribute("admin", admin);

        Mockito.when(productService.findAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/admin/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void showDashboard_NoSession_RedirectLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login"));
    }

    @Test
    void showForgotPassword_ReturnView() throws Exception {
        mockMvc.perform(get("/admin/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/forgot-password"));
    }

    @Test
    void showResetPassword_WithToken_ReturnView() throws Exception {
        mockMvc.perform(get("/admin/reset-password").param("token", "test-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reset-password"))
                .andExpect(model().attribute("token", "test-token"));
    }

    @Test
    void logout_InvalidateSession_RedirectLogin() throws Exception {
        session.setAttribute("admin", new Admin());

        mockMvc.perform(post("/admin/logout").session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login"));
    }

    @Test
    void logoutApi_Success_ReturnTrue() throws Exception {
        session.setAttribute("admin", new Admin());

        mockMvc.perform(post("/admin/api/logout").session(session).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ログアウトしました"));
    }

    @Test
    void logoutApi_Exception_ReturnError() throws Exception {
        MockHttpSession faultySession = Mockito.mock(MockHttpSession.class);
        doThrow(new IllegalStateException("session error")).when(faultySession).invalidate();

        mockMvc.perform(post("/admin/api/logout").session(faultySession).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("ログアウト処理中にエラーが発生しました"));
    }

    @Test
    void getLoginStatus_WithSession_ReturnAdminInfo() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setName("システム管理者");
        admin.setEmail("admin@example.com");
        session.setAttribute("admin", admin);

        mockMvc.perform(get("/admin/api/status").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loggedIn").value(true))
                .andExpect(jsonPath("$.admin.username").value("admin"))
                .andExpect(jsonPath("$.admin.name").value("システム管理者"))
                .andExpect(jsonPath("$.admin.email").value("admin@example.com"));
    }

    @Test
    void getLoginStatus_NoSession_ReturnFalse() throws Exception {
        mockMvc.perform(get("/admin/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loggedIn").value(false));
    }

    @Test
    void getLoginStatus_Exception_ReturnFalse() throws Exception {
        MockHttpSession faultySession = Mockito.mock(MockHttpSession.class);
        Mockito.when(faultySession.getAttribute("admin")).thenThrow(new RuntimeException("internal error"));

        mockMvc.perform(get("/admin/api/status").session(faultySession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loggedIn").value(false));
    }
}
