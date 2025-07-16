package com.example.simplezakka.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopController.class)
class TopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 正常系：/top にアクセスしてビュー名 "top" を返す
    @Test
    void testTopPageReturnsTopView() throws Exception {
        mockMvc.perform(get("/top"))
                .andExpect(status().isOk())
                .andExpect(view().name("top"));
    }

    // 異常系：POSTで /top にアクセスした場合、405エラー
    @Test
    void testTopPageWithPostMethod() throws Exception {
        mockMvc.perform(post("/top"))
                .andExpect(status().isMethodNotAllowed());
    }

    // 異常系：存在しないURLにアクセス → 404エラー
    @Test
    void testInvalidUrlReturns404() throws Exception {
        mockMvc.perform(get("/invalid-url"))
                .andExpect(status().isNotFound());
    }
}
