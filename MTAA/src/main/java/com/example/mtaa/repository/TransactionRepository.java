package com.example.mtaa.repository;

import com.example.mtaa.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

    public List<Transaction> findByUser_Username(String username);
    public List<Transaction> findByUser_UsernameAndCreationDateBetween(String username, LocalDateTime start, LocalDateTime end);
    public List<Transaction> findByBudgetId(Long budgetId);
}
