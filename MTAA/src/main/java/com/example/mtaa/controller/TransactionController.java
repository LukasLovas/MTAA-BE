package com.example.mtaa.controller;

import com.example.mtaa.dto.CategorySpendingDTO;
import com.example.mtaa.dto.TransactionDTO;
import com.example.mtaa.model.Transaction;
import com.example.mtaa.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("")
    public List<Transaction> getAllTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getAllTransactions(username);
    }

    @GetMapping("/expenses/today")
    public List<CategorySpendingDTO> getExpensesToday() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getExpensesToday(username);
    }

    @GetMapping("/expenses/week")
    public List<CategorySpendingDTO> getExpensesWeek() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getExpensesForWeek(username);
    }

    @GetMapping("/expenses/month")
    public List<CategorySpendingDTO> getExpensesMonth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getExpensesForMonth(username);
    }

    @PostMapping("")
    public ResponseEntity<Transaction> addTransaction(@RequestBody @Validated TransactionDTO transactionInput) {
        return ResponseEntity.ok(transactionService.addTransaction(transactionInput));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/budget/{budgetId}")
    public List<Transaction> getTransactionsByBudgetId(@PathVariable Integer budgetId) {
        return transactionService.getTransactionsByBudgetId(budgetId);
    }

    @PutMapping("{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Integer id, @RequestBody @Validated TransactionDTO transactionInput) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionInput));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Transaction> deleteTransaction(@PathVariable Integer id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
