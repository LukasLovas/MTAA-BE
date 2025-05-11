package com.example.mtaa.service;

import com.example.mtaa.dto.BudgetDTO;
import com.example.mtaa.model.Budget;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.enums.IntervalEnum;
import com.example.mtaa.repository.BudgetRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserService userService;

    public BudgetService(BudgetRepository budgetRepository, UserService userService) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
    }

    public Budget addBudget(BudgetDTO input) {
        Budget budget = convertToBudget(input);
        return budgetRepository.save(budget);
    }

    public Budget getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, "Budget with ID " + budgetId + " not found"));
    }

    public Budget updateBudget(Long id, BudgetDTO input) {
        Budget budget = budgetRepository.findById(id).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, String.format("Budget with ID %s was not found", id)));
        budget.setLabel(input.getLabel());
        budget.setInitialAmount(input.getAmount());
        budget.setIntervalValue(input.getIntervalValue());
        budget.setIntervalEnum(IntervalEnum.valueOf(input.getIntervalEnum()));
        budget.setStartDate(input.getStartDate());

        budgetRepository.save(budget);
        return budget;
    }

    public Budget updateBudget(Long id, Budget budget) {
        Budget existingBudget = budgetRepository.findById(id).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, String.format("Budget with ID %s was not found", id)));
        existingBudget.setLabel(budget.getLabel());
        existingBudget.setAmount(budget.getAmount());
        existingBudget.setIntervalValue(budget.getIntervalValue());
        existingBudget.setIntervalEnum(budget.getIntervalEnum());
        existingBudget.setStartDate(budget.getStartDate());

        return budgetRepository.save(existingBudget);
    }

    public void deleteBudget(Long id) {
        try{
            budgetRepository.deleteById(id);
        }catch(Exception e){
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
    }

    public List<Budget> getAllBudgets(String username) {
        return budgetRepository.findByUser_Username(username);
    }

    private Budget convertToBudget(BudgetDTO input) {
        Budget budget = new Budget();
        budget.setUser(userService.findUserById(userService.findCurrentUser().getId()));
        budget.setLabel(input.getLabel());
        budget.setInitialAmount(input.getAmount());
        budget.setAmount(input.getAmount());
        budget.setIntervalValue(input.getIntervalValue());
        budget.setIntervalEnum(IntervalEnum.valueOf(input.getIntervalEnum()));
        budget.setStartDate(input.getStartDate());
        budget.setLastResetDate(input.getStartDate());
        return budget;
    }

    @Transactional
    public void resetBudgets() {
        List<Budget> budgets = budgetRepository.findAll();
        LocalDateTime today = LocalDateTime.now();
        int counter = 0;
        for (Budget budget : budgets) {
            if (shouldResetBudget(budget, today)) {
                log.info("Resetting budget {} for user {}", budget.getLabel(), budget.getUser().getId());
                counter++;
                budget.setAmount(budget.getInitialAmount());
                budget.setLastResetDate(today);
                budgetRepository.save(budget);
            }
        }
        if (counter == 0){
            log.info("No budgets were due for reseting.");
        }
    }

    private boolean shouldResetBudget(Budget budget, LocalDateTime today) {
        if (budget.getLastResetDate() == null) {
            return true;
        }

        return switch (budget.getIntervalEnum()) {
            case DAY -> budget.getLastResetDate().isBefore(today);
            case WEEK -> budget.getLastResetDate().isBefore(today.minusWeeks(1));
            case MONTH -> budget.getLastResetDate().isBefore(today.minusMonths(1));
            case YEAR -> budget.getLastResetDate().isBefore(today.minusYears(1));
            default -> false;
        };
    }
}
