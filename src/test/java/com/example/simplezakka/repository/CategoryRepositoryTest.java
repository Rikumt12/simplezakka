package com.example.simplezakka.repository;

import com.example.simplezakka.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("正常: 新しいカテゴリを保存できる")
    void saveCategory_ShouldSaveSuccessfully() {
        Category category = new Category(null, "家電", null, null);
        Category saved = categoryRepository.save(category);

        assertThat(saved.getCategoryId()).isNotNull();
        assertThat(saved.getCategoryName()).isEqualTo("家電");
    }

    @Test
    @DisplayName("異常: カテゴリ名がnullの場合は例外が発生する")
    void saveCategory_WithNullCategoryName_ShouldThrowException() {
        Category category = new Category(null, null, null, null);
        assertThatThrownBy(() -> categoryRepository.saveAndFlush(category))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("正常: 存在しないIDをfindByIdした場合はemptyを返す")
    void findById_WhenCategoryDoesNotExist_ShouldReturnEmpty() {
        Optional<Category> result = categoryRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("正常: ID指定でカテゴリを取得できる")
    void findById_WhenCategoryExists_ShouldReturnCategory() {
        Category category = categoryRepository.save(new Category(null, "書籍", null, null));
        Optional<Category> result = categoryRepository.findById(category.getCategoryId());
        assertThat(result).isPresent();
        assertThat(result.get().getCategoryName()).isEqualTo("書籍");
    }

    @Test
    @DisplayName("正常: Orderが存在しない場合、findAllで空リストが返る")
    void findAll_WhenNoOrders_ShouldReturnEmptyList() {
        List<Category> all = categoryRepository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    @DisplayName("正常: 複数のカテゴリを全件取得できる")
    void findAll_ShouldReturnAllCategories() {
        categoryRepository.save(new Category(null, "食品", null, null));
        categoryRepository.save(new Category(null, "衣料品", null, null));

        List<Category> all = categoryRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("正常: カテゴリが存在しない場合、findAllは空リスト")
    void findAll_WhenNoCategoryExists_ShouldReturnEmptyList() {
        List<Category> all = categoryRepository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    @DisplayName("正常: カテゴリの更新が反映される")
    void updateCategory_ShouldReflectChanges() {
        Category category = categoryRepository.save(new Category(null, "雑貨", null, null));
        category.setCategoryName("雑貨（更新）");

        Category updated = categoryRepository.save(category);
        assertThat(updated.getCategoryName()).isEqualTo("雑貨（更新）");
    }

    @Test
    @DisplayName("正常: カテゴリが削除される")
    void deleteById_ShouldDeleteCategory() {
        Category category = categoryRepository.save(new Category(null, "削除対象", null, null));
        categoryRepository.deleteById(category.getCategoryId());

        Optional<Category> result = categoryRepository.findById(category.getCategoryId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("存在しないIDをdeleteしても例外は発生しない")
    void deleteById_WhenCategoryNotExists_ShouldNotThrowException() {
    assertThatCode(() -> categoryRepository.deleteById(999))
        .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("正常: findByCategoryNameでカテゴリ取得")
    void findByCategoryName_WhenExists_ShouldReturnCategory() {
        Category saved = categoryRepository.save(new Category(null, "食品", null, null));
        Optional<Category> result = categoryRepository.findByCategoryName("食品");

        assertThat(result).isPresent();
        assertThat(result.get().getCategoryId()).isEqualTo(saved.getCategoryId());
    }

    @Test
    @DisplayName("正常: findByCategoryNameでカテゴリが見つからない場合はempty")
    void findByCategoryName_WhenNotExists_ShouldReturnEmpty() {
        Optional<Category> result = categoryRepository.findByCategoryName("架空カテゴリ");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("カテゴリ名にnullを渡すとemptyを返す")
    void findByCategoryName_WithNull_ShouldReturnEmpty() {
    Optional<Category> result = categoryRepository.findByCategoryName(null);
    assertThat(result).isEmpty();
    }
}