package com.example.mtaa.controller;

import com.example.mtaa.model.Transaction;
import com.example.mtaa.service.TransactionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping("")
    public Transaction addTransaction(@RequestBody @Validated Transaction transaction) {
        return transactionService.addTransaction(transaction);
    }

}
