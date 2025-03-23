package com.example.mtaa.service;

import com.example.mtaa.dto.TransactionDTO;
import com.example.mtaa.model.Budget;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.enums.FrequencyEnum;
import com.example.mtaa.model.enums.TransactionTypeEnum;
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

    public Transaction addTransaction(TransactionDTO transactionInput){
        Transaction transaction = convertToTransaction(transactionInput);
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

    public List<Transaction> getTransactionsByBudgetId(Integer budgetId) {
        return transactionRepository.findByBudgetId(budgetId);
    }

    private Transaction convertToTransaction(TransactionDTO input) {
        Transaction transaction = new Transaction();
        transaction.setLabel(input.getLabel());
        transaction.setAmount(input.getAmount());
        transaction.setCreationDate(input.getTimestamp());
        transaction.setAttachmentId(input.getAttachmentId());
        transaction.setBudget(new Budget(input.getBudgetId(), null, null, null, null, null));
        transaction.setCategoryId(input.getCategoryId());
        transaction.setFrequencyEnum(FrequencyEnum.valueOf(input.getFrequencyEnum().toUpperCase()));
        transaction.setNote(input.getNote());
        transaction.setTransactionTypeEnum(TransactionTypeEnum.valueOf(input.getTransactionTypeEnum()));
        return transaction;
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setLabel(transaction.getLabel());
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setTimestamp(transaction.getCreationDate());
        transactionDTO.setAttachmentId(transaction.getAttachmentId());
        transactionDTO.setBudgetId(transaction.getBudget().getId());
        transactionDTO.setCategoryId(transaction.getCategoryId());
        transactionDTO.setFrequencyEnum(transaction.getFrequencyEnum().name());
        transactionDTO.setNote(transaction.getNote());
        transactionDTO.setTransactionTypeEnum(transaction.getTransactionTypeEnum().name());
        return transactionDTO;
    }

}
