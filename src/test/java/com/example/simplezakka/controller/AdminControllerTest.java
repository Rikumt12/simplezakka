package com.example.simplezakka.controller;

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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("ログイン処理中にエラーが発生しました"));
    }
}