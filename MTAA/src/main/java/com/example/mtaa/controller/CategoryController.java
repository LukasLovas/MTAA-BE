package com.example.mtaa.controller;

import com.example.mtaa.dto.CategoryDTO;
import com.example.mtaa.model.Category;
import com.example.mtaa.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Retrieves all existing categories", description = "Queries the database for a complete list of all existing Category objects.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("")
    public List<Category> getAllCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return categoryService.getAllCategories(username);
    }

    @Operation(summary = "Retrieves a category by ID.", description = "Queries the databse for a Category object based on ID from the user input.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @Operation(summary = "Creates a category", description = "Creates and saves a new Category object into the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Category with label name already exists for this user.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("")
    public Category addCategory(@RequestBody @Validated CategoryDTO category) {
        return categoryService.addCategory(category);
    }

    @Operation(summary = "Updates a category", description = "Updates and saves a Category object in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, CategoryDTO category) {
        return categoryService.updateCategory(id, category);
    }

    @Operation(summary = "Deletes a category", description = "Deletes a Category object in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }


}
