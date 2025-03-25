package com.example.mtaa.repository;

import com.example.mtaa.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser_Username(String username);
}
