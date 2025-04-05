package com.example.mtaa.service;

import com.example.mtaa.model.Category;
import com.example.mtaa.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories(String username) {
        return categoryRepository.findByUser_Username(username);
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        categoryToUpdate.setLabel(category.getLabel());
        return categoryRepository.save(categoryToUpdate);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }



}
