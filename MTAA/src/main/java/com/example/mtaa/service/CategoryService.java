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
    private final UserService userService;
    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<Category> getAllCategories(String username) {
        return categoryRepository.findByUser_Username(username);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, "Category with ID " + id + " not found"));
    }

    public Category addCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.findCategoryByLabelAndUser_Id(categoryDTO.getLabel(), categoryDTO.getUserId()).isEmpty()){
            Category category = convertToCategory(categoryDTO);
            return categoryRepository.save(category);
        }else{
            throw new CommonException(HttpStatus.BAD_REQUEST, "Category with label name " + categoryDTO.getLabel() + " already exists for user with id " + categoryDTO.getUserId());
        }
    }

    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "Category not found"));
        categoryToUpdate.setLabel(categoryDTO.getLabel());
        return categoryRepository.save(categoryToUpdate);
    }

    public void deleteCategory(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
    }

    private Category convertToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setLabel(categoryDTO.getLabel());
        category.setUser(userService.findUserById(categoryDTO.getUserId()));
        return category;
    }

}
