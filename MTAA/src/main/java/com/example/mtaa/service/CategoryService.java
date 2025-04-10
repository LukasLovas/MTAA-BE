package com.example.mtaa.service;

import com.example.mtaa.dto.CategoryDTO;
import com.example.mtaa.model.Category;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
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

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, "Category with ID " + id + " not found"));
    }

    public Category addCategory(CategoryDTO categoryDTO) {
        Category category = convertToCategory(categoryDTO);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        categoryToUpdate.setLabel(categoryDTO.getLabel());
        return categoryRepository.save(categoryToUpdate);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private Category convertToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setLabel(categoryDTO.getLabel());
        category.setUser(new com.example.mtaa.model.User(categoryDTO.getUserId(), null, null, true));
        return category;
    }

}
