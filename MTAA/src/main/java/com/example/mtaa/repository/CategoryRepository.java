package com.example.mtaa.repository;

import com.example.mtaa.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser_Username(String username);

    Optional<Category> findCategoryByLabelAndUser_Id(String label, Long user_id);
}
