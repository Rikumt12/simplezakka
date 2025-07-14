package com.example.simplezakka.service;
 
import com.example.simplezakka.entity.Category;
import com.example.simplezakka.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public class CategoryService {
 
    private final CategoryRepository categoryRepository;
 
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
 
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}