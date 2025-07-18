package com.example.simplezakka.controller;
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.service.AdminService;
import com.example.simplezakka.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
<<<<<<< HEAD

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
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

=======
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AdminController.class)
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
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void showLogin_NoSession_ReturnLoginView() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }
<<<<<<< HEAD

    @Test
    void showLogin_WithSession_RedirectDashboard() throws Exception {
        session.setAttribute("admin", new Admin());

=======
    @Test
    void showLogin_WithSession_RedirectDashboard() throws Exception {
        session.setAttribute("admin", new Admin());
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        mockMvc.perform(get("/admin/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void login_ValidCredentials_ReturnSuccess() throws Exception {
        Admin dummyAdmin = new Admin();
        dummyAdmin.setUsername("admin");
        dummyAdmin.setName("システム管理者");
        dummyAdmin.setEmail("admin@simplezakka.com");
<<<<<<< HEAD

        Mockito.when(adminService.authenticate("admin", "admin123"))
                .thenReturn(dummyAdmin);

=======
        Mockito.when(adminService.authenticate("admin", "admin123"))
                .thenReturn(dummyAdmin);
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        Map<String, String> requestBody = Map.of(
                "username", "admin",
                "password", "admin123"
        );
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        mockMvc.perform(post("/admin/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ログインしました"));
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void login_InvalidCredentials_ReturnError() throws Exception {
        Mockito.when(adminService.authenticate("admin", "wrongpass"))
                .thenReturn(null);
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        Map<String, String> requestBody = Map.of(
                "username", "admin",
                "password", "wrongpass"
        );
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        mockMvc.perform(post("/admin/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("管理者IDまたはパスワードが間違っています"));
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void login_EmptyCredentials_ReturnError() throws Exception {
        Map<String, String> requestBody = Map.of(
                "username", "",
                "password", ""
        );
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        mockMvc.perform(post("/admin/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("管理者IDとパスワードを入力してください"));
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void login_Exception_ReturnError() throws Exception {
        Mockito.when(adminService.authenticate(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("DB error"));
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        Map<String, String> requestBody = Map.of(
                "username", "admin",
                "password", "admin123"
        );
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        mockMvc.perform(post("/admin/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("ログイン処理中にエラーが発生しました"));
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
}
