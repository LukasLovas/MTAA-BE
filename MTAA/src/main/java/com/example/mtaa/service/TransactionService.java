package com.example.mtaa.service;

import com.example.mtaa.dto.TransactionDTO;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.repository.TransactionRepository;
import com.example.mtaa.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return transactionRepository.findById(transactionId).orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    public Transaction updateTransaction(int id, TransactionDTO input) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        Transaction transactionToUpdate;
        if (transaction.isPresent()) {
            transactionToUpdate = transaction.get();
            transactionToUpdate.setLabel(input.getLabel());
            transactionToUpdate.setAmount(input.getAmount());
            transactionRepository.save(transactionToUpdate);
        } else {
            throw new CommonException(HttpStatus.NOT_FOUND, String.format("Transaction with ID %s does not exist", id));
        }

        return transactionRepository.findById(id).get();
    }

    public void deleteTransaction(Integer id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}
