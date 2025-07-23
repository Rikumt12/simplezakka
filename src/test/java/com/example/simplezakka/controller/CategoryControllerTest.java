package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Category;
import com.example.simplezakka.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WithMockUser 
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Category> testCategories;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        Category category1 = new Category(1, "電子機器", testDateTime, testDateTime);
        Category category2 = new Category(2, "書籍", testDateTime, testDateTime);
        Category category3 = new Category(3, "食品", testDateTime, testDateTime);
        
        testCategories = Arrays.asList(category1, category2, category3);
    }

    @Test
    @DisplayName("カテゴリ一覧取得 - 複数データが存在する場合")
    void testGetAllCategoriesReturnsList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(testCategories);

   
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].categoryId").value(1))
                .andExpect(jsonPath("$[0].categoryName").value("電子機器"))
                .andExpect(jsonPath("$[1].categoryId").value(2))
                .andExpect(jsonPath("$[1].categoryName").value("書籍"))
                .andExpect(jsonPath("$[2].categoryId").value(3))
                .andExpect(jsonPath("$[2].categoryName").value("食品"));
    }

    @Test
    @DisplayName("カテゴリ一覧取得 - データが存在しない場合")
    void testGetAllCategoriesReturnsEmptyList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("エンドポイントの存在確認 - GETメソッドで正常にアクセス可能")
    void testGetAllCategoriesStatusOk() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(testCategories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @DisplayName("HTTPメソッド制限確認 - POSTメソッドは許可されていない")
    void testPostMethodNotAllowed() throws Exception {
 
        mockMvc.perform(post("/api/categories")
                .with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
}