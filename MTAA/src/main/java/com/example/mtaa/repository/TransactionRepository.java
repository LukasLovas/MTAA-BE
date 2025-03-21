package com.example.mtaa.repository;

import com.example.mtaa.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
    public List<Transaction> findByBudgetId(Integer budgetId);
}
