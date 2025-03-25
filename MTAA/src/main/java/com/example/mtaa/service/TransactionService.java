package com.example.mtaa.service;

import com.example.mtaa.dto.CategorySpendingDTO;
import com.example.mtaa.dto.TransactionDTO;
import com.example.mtaa.model.*;
import com.example.mtaa.model.enums.FrequencyEnum;
import com.example.mtaa.model.enums.TransactionTypeEnum;
import com.example.mtaa.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<Transaction> getAllTransactions(String username) {
        return transactionRepository.findByUser_Username(username);
    }

    public List<CategorySpendingDTO> getExpensesToday(String username) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = today.withHour(23).withMinute(59).withSecond(59);

        List<Transaction> transactions = transactionRepository.findByUser_UsernameAndCreationDateBetween(username, start, end);

        return getCategoryExpenses(transactions);
    }

    public List<CategorySpendingDTO> getExpensesForWeek(String username) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.withHour(0).withMinute(0).withSecond(0).minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDateTime end = today.withHour(23).withMinute(59).withSecond(59).plusDays(7 - today.getDayOfWeek().getValue());

        List<Transaction> transactions = transactionRepository.findByUser_UsernameAndCreationDateBetween(username, start, end);

        return getCategoryExpenses(transactions);
    }

    public List<CategorySpendingDTO> getExpensesForMonth(String username) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.withHour(0).withMinute(0).withSecond(0).withDayOfMonth(1);
        LocalDateTime end = today.withHour(23).withMinute(59).withSecond(59).plusMonths(1).withDayOfMonth(1).minusDays(1);

        List<Transaction> transactions = transactionRepository.findByUser_UsernameAndCreationDateBetween(username, start, end);

        return getCategoryExpenses(transactions);
    }

    private List<CategorySpendingDTO> getCategoryExpenses(List<Transaction> transactions) {
        List<CategorySpendingDTO> categorySpendingDTOs = new ArrayList<>();

        for (Transaction transaction : transactions) {
            boolean found = false;

            for (CategorySpendingDTO categorySpendingDTO : categorySpendingDTOs) {
                if (categorySpendingDTO.getCategoryName().equals(transaction.getCategory().getLabel())) {
                    if(transaction.getTransactionTypeEnum().equals(TransactionTypeEnum.EXPENSE))
                        categorySpendingDTO.setAmount(categorySpendingDTO.getAmount() + transaction.getAmount());
                    else
                        categorySpendingDTO.setAmount(categorySpendingDTO.getAmount() - transaction.getAmount());

                    found = true;
                    break;
                }
            }

            if (!found) {
                CategorySpendingDTO categorySpendingDTO = new CategorySpendingDTO();
                categorySpendingDTO.setCategoryName(transaction.getCategory().getLabel());
                if(transaction.getTransactionTypeEnum().equals(TransactionTypeEnum.EXPENSE))
                    categorySpendingDTO.setAmount(transaction.getAmount());
                else
                    categorySpendingDTO.setAmount(-transaction.getAmount());

                categorySpendingDTOs.add(categorySpendingDTO);
            }
        }

        return categorySpendingDTOs;
    }

    public List<Transaction> getTransactionsByBudgetId(Integer budgetId) {
        return transactionRepository.findByBudgetId(budgetId);
    }

    private Transaction convertToTransaction(TransactionDTO input) {
        Transaction transaction = new Transaction();
        transaction.setUser(new User(input.getUserId(), null, null, true));
        transaction.setLabel(input.getLabel());
        transaction.setAmount(input.getAmount());
        transaction.setCreationDate(input.getTimestamp());
        transaction.setAttachmentId(input.getAttachmentId());
        transaction.setBudget(new Budget(input.getBudgetId(), null, null, null, null, null, null));
        transaction.setCategory(new Category(input.getCategoryId(), null, null));
        transaction.setFrequencyEnum(FrequencyEnum.valueOf(input.getFrequencyEnum().toUpperCase()));
        transaction.setNote(input.getNote());
        transaction.setTransactionTypeEnum(TransactionTypeEnum.valueOf(input.getTransactionTypeEnum()));
        return transaction;
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setUserId(transaction.getUser().getId());
        transactionDTO.setLabel(transaction.getLabel());
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setTimestamp(transaction.getCreationDate());
        transactionDTO.setAttachmentId(transaction.getAttachmentId());
        transactionDTO.setBudgetId(transaction.getBudget().getId());
        transactionDTO.setCategoryId(transaction.getCategory().getId());
        transactionDTO.setFrequencyEnum(transaction.getFrequencyEnum().name());
        transactionDTO.setNote(transaction.getNote());
        transactionDTO.setTransactionTypeEnum(transaction.getTransactionTypeEnum().name());
        return transactionDTO;
    }

}
