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

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final BudgetService budgetService;
    private final CategoryService categoryService;

    private final LocationService locationService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService,
                              BudgetService budgetService, CategoryService categoryService, LocationService locationService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.locationService = locationService;
    }

    public Transaction addTransaction(TransactionDTO transactionInput){
        Transaction transaction = convertToTransaction(transactionInput);
        if (transactionInput.getBudgetId() != null){
            Budget budget = budgetService.getBudgetById(transactionInput.getBudgetId());
            if(transaction.getCreationDate().isAfter(budget.getLastResetDate().atStartOfDay())){
                double newAmount;
                if(transaction.getTransactionTypeEnum().equals(TransactionTypeEnum.EXPENSE)){
                    newAmount = budget.getAmount() - transaction.getAmount();
                }
                else{
                    newAmount = budget.getAmount() + transaction.getAmount();
                }
                budget.setAmount(newAmount);
                budgetService.updateBudget(budget.getId(), budget);
            }
        }
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    public Transaction updateTransaction(Long id, TransactionDTO input) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        Transaction transactionToUpdate;
        if (transaction.isPresent()) {
            transactionToUpdate = transaction.get();

            if(!transactionToUpdate.getAmount().equals(input.getAmount())){
                if (input.getBudgetId() != null){
                    Budget budget = budgetService.getBudgetById(input.getBudgetId());
                    if(transactionToUpdate.getCreationDate().isAfter(budget.getLastResetDate().atStartOfDay())){
                        double newAmount;
                        if(transactionToUpdate.getTransactionTypeEnum().equals(TransactionTypeEnum.EXPENSE)){
                            newAmount = budget.getAmount() + transactionToUpdate.getAmount() - input.getAmount();
                        }
                        else{
                            newAmount = budget.getAmount() - transactionToUpdate.getAmount() + input.getAmount();
                        }
                        budget.setAmount(newAmount);
                        budgetService.updateBudget(budget.getId(), budget);
                    }
                }
            }

            transactionToUpdate.setLabel(input.getLabel());
            transactionToUpdate.setAmount(input.getAmount());

            transactionRepository.save(transactionToUpdate);
        } else {
            throw new CommonException(HttpStatus.NOT_FOUND, String.format("Transaction with ID %s was not found.", id));
        }

        return transactionRepository.findById(id).get();
    }

    public void deleteTransaction(Long id) {
        try{
            Transaction transaction = transactionRepository.findById(id).orElseThrow(() ->
                    new CommonException(HttpStatus.NOT_FOUND, String.format("Transaction with ID %s was not found", id)));

            Budget budget = budgetService.getBudgetById(transaction.getBudget().getId());

            if(transaction.getCreationDate().isAfter(budget.getLastResetDate().atStartOfDay())){
                double newAmount;
                if(transaction.getTransactionTypeEnum().equals(TransactionTypeEnum.EXPENSE)){
                    newAmount = budget.getAmount() + transaction.getAmount();
                }
                else{
                    newAmount = budget.getAmount() - transaction.getAmount();
                }
                budget.setAmount(newAmount);
                budgetService.updateBudget(budget.getId(), budget);
            }

            transactionRepository.deleteById(id);
        }catch(Exception e){
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
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

    public List<Transaction> getTransactionsByBudgetId(Long budgetId) {
        return transactionRepository.findByBudgetId(budgetId);
    }

    private Transaction convertToTransaction(TransactionDTO input) {
        Transaction transaction = new Transaction();
        transaction.setUser(userService.findUserById(input.getUserId()));
        transaction.setLabel(input.getLabel());
        transaction.setAmount(input.getAmount());
        transaction.setCreationDate(input.getTimestamp());
        transaction.setFilename(input.getFilename());
        transaction.setBudget(input.getBudgetId() != null ? budgetService.getBudgetById(input.getBudgetId()) : null);
        transaction.setCategory(input.getCategoryId() != null ? categoryService.getCategoryById(input.getCategoryId()) : null);
        transaction.setFrequencyEnum(FrequencyEnum.valueOf(input.getFrequencyEnum().toUpperCase()));
        transaction.setNote(input.getNote());
        transaction.setTransactionTypeEnum(TransactionTypeEnum.valueOf(input.getTransactionTypeEnum()));
        transaction.setLocation(input.getLocationId() != null ? locationService.getLocationById(input.getLocationId()) : null);
        return transaction;
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setUserId(transaction.getUser().getId());
        transactionDTO.setLabel(transaction.getLabel());
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setTimestamp(transaction.getCreationDate());
        transactionDTO.setFilename(transaction.getFilename());
        transactionDTO.setBudgetId(transaction.getBudget().getId());
        transactionDTO.setCategoryId(transaction.getCategory().getId());
        transactionDTO.setFrequencyEnum(transaction.getFrequencyEnum().name());
        transactionDTO.setNote(transaction.getNote());
        transactionDTO.setTransactionTypeEnum(transaction.getTransactionTypeEnum().name());
        return transactionDTO;
    }

}
