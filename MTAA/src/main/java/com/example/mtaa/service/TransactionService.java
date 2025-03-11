package com.example.mtaa.service;

import com.example.mtaa.repository.TransactionRepository;
import com.example.mtaa.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction addTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(int transactionId) {
        return transactionRepository.findById(transactionId).get();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}
