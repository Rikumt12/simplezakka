package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Category;
import com.example.simplezakka.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false) // ✅ Spring Securityのフィルター無効化
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("カテゴリ一覧取得：カテゴリが複数登録されている状態")
    void testGetAllCategoriesReturnsList() throws Exception {
    List<Category> categories = Arrays.asList(
        new Category(1, "キッチン"),
        new Category(2, "インテリア")
    );

    when(categoryService.getAllCategories()).thenReturn(categories);

    mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].categoryName").value("キッチン"))
            .andExpect(jsonPath("$[1].categoryName").value("インテリア"));
    }


    @Test
    @DisplayName("カテゴリ一覧取得：カテゴリが1件も登録されていない状態")
    void testGetAllCategoriesReturnsEmptyList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("エンドポイントの存在確認：HTTP 200が返る")
    void testGetAllCategoriesStatusOk() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("HTTPメソッド制限確認：POSTでアクセスした場合は405")
    void testPostMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/categories"))
                .andExpect(status().isMethodNotAllowed());
    }
}