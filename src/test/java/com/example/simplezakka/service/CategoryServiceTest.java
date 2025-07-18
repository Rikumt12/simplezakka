package com.example.simplezakka.service;

import com.example.simplezakka.entity.Category;
import com.example.simplezakka.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CategoryService categoryService = new CategoryService(categoryRepository);

    @Test
    @DisplayName("カテゴリ一覧取得：カテゴリが複数登録されている状態")
    void testGetAllCategoriesReturnsList() {
        List<Category> mockList = Arrays.asList(
                new Category(1, "キッチン"),
                new Category(2, "インテリア")
        );

        when(categoryRepository.findAll()).thenReturn(mockList);

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryName()).isEqualTo("キッチン");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("カテゴリ一覧取得：カテゴリが1件も登録されていない状態")
    void testGetAllCategoriesReturnsEmptyList() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("カテゴリ一覧取得：リポジトリが例外をスローした場合")
    void testGetAllCategoriesThrowsException() {
        when(categoryRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> categoryService.getAllCategories())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");

        verify(categoryRepository, times(1)).findAll();
    }
}