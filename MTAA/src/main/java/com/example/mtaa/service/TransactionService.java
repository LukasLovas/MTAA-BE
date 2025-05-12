package com.example.mtaa.service;

import com.example.mtaa.dto.CategorySpendingDTO;
import com.example.mtaa.dto.TransactionDTO;
import com.example.mtaa.event.TransactionChangedEvent;
import com.example.mtaa.model.*;
import com.example.mtaa.model.enums.FrequencyEnum;
import com.example.mtaa.model.enums.TransactionTypeEnum;
import com.example.mtaa.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            UserService userService,
            BudgetService budgetService,
            CategoryService categoryService,
            LocationService locationService,
            ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.eventPublisher = eventPublisher;
    }

    public Transaction addTransaction(TransactionDTO transactionInput){
        Transaction transaction = convertToTransaction(transactionInput);
        if (transaction.getBudget() != null){
            Budget budget = transaction.getBudget();
            if(transaction.getCreationDate().isAfter(budget.getLastResetDate().with(LocalTime.MIN))){
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
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Vysielame udalosť namiesto priameho volania webSocketHandler
        eventPublisher.publishEvent(new TransactionChangedEvent(this, transaction.getUser().getId()));

        return savedTransaction;
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
                if (input.getBudget() != null) {
                    Long userId = userService.findCurrentUser().getId();
                    Budget budget = getBudgetByLabelAndUserId(input.getBudget(), userId);
                    if(transactionToUpdate.getCreationDate().isAfter(budget.getLastResetDate().with(LocalTime.MIN))){
                        double newAmount;
                        if(transactionToUpdate.getTransactionTypeEnum().equals(TransactionTypeEnum.EXPENSE)){
                            newAmount = budget.getAmount() + transactionToUpdate.getAmount() - input.getAmount();
                        } else {
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

            eventPublisher.publishEvent(new TransactionChangedEvent(this, transactionToUpdate.getUser().getId()));

        } else {
            throw new CommonException(HttpStatus.NOT_FOUND, String.format("Transaction with ID %s was not found.", id));
        }

        return transactionRepository.findById(id).get();
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        Long userId = transaction.getUser().getId();

        Budget budget = budgetService.getBudgetById(transaction.getBudget().getId());

        if(transaction.getCreationDate().isAfter(budget.getLastResetDate().with(LocalTime.MIN))){
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

        // Vysielame udalosť namiesto priameho volania webSocketHandler
        eventPublisher.publishEvent(new TransactionChangedEvent(this, userId));
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

    private Budget getBudgetByLabelAndUserId(String label, Long userId) {
        return budgetService.getBudgetByLabel(label, userId);
    }

    private Transaction convertToTransaction(TransactionDTO input) {
        User user = userService.findCurrentUser();
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setLabel(input.getLabel());
        transaction.setAmount(input.getAmount());
        transaction.setCreationDate(input.getTimestamp());
        transaction.setFilename(input.getFilename());
        transaction.setBudget(input.getBudget() != null ? budgetService.getBudgetByLabel(input.getBudget(), user.getId()) : null);
        transaction.setCategory(input.getCategory() != null ? categoryService.getCategoryByLabel(input.getCategory(), user.getId()) : null);
        transaction.setFrequencyEnum(FrequencyEnum.valueOf(input.getFrequencyEnum().toUpperCase()));
        transaction.setNote(input.getNote());
        transaction.setTransactionTypeEnum(TransactionTypeEnum.valueOf(input.getTransactionTypeEnum()));
        transaction.setLocation(input.getLocationId() != null ? locationService.getLocationById(input.getLocationId()) : null);
        return transaction;
    }
}
