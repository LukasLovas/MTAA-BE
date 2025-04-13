package com.example.mtaa.repository;

import com.example.mtaa.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    public List<Budget> findByUser_Username(String username);
}
