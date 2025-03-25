package com.example.mtaa.repository;

import com.example.mtaa.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    public List<Budget> findByUser_Username(String username);
}
