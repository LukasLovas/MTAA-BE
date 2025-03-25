package com.example.mtaa.repository;

import com.example.mtaa.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>{

    public List<Transaction> findByUser_Username(String username);
    public List<Transaction> findByUser_UsernameAndCreationDateBetween(String username, LocalDateTime start, LocalDateTime end);
    public List<Transaction> findByBudgetId(Integer budgetId);
}
