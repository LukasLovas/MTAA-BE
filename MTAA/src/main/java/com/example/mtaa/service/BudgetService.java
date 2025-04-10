package com.example.mtaa.service;

import com.example.mtaa.dto.BudgetDTO;
import com.example.mtaa.model.Budget;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.User;
import com.example.mtaa.model.enums.IntervalEnum;
import com.example.mtaa.repository.BudgetRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget addBudget(BudgetDTO input) {
        Budget budget = convertToBudget(input);
        return budgetRepository.save(budget);
    }

    public Budget getBudgetById(Integer budgetId) {
        return budgetRepository.findById(budgetId).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, "Budget with ID " + budgetId + "not found"));
    }

    public Budget updateBudget(int id, BudgetDTO input) {
        Budget budget = budgetRepository.findById(id).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, String.format("Budget with ID %s does not exist", id)));
        budget.setLabel(input.getLabel());
        budget.setAmount(input.getAmount());
        budgetRepository.save(budget);
        return budget;
    }

    public void deleteBudget(Integer id) {
        budgetRepository.deleteById(id);
    }

    public List<Budget> getAllBudgets(String username) {
        return budgetRepository.findByUser_Username(username);
    }

    private Budget convertToBudget(BudgetDTO input) {
        Budget budget = new Budget();
        budget.setUser(new User(input.getUserId(), null, null, true));
        budget.setLabel(input.getLabel());
        budget.setAmount(input.getAmount());
        budget.setIntervalValue(input.getIntervalValue());
        budget.setIntervalEnum(IntervalEnum.valueOf(input.getIntervalEnum()));
        budget.setStartDate(input.getStartDate());
        return budget;
    }

    private BudgetDTO convertToBudgetDTO(Budget budget) {
        BudgetDTO budgetDTO = new BudgetDTO();
        budgetDTO.setUserId(budget.getUser().getId());
        budgetDTO.setLabel(budget.getLabel());
        budgetDTO.setAmount(budget.getAmount());
        budgetDTO.setIntervalValue(budget.getIntervalValue());
        budgetDTO.setIntervalEnum(budget.getIntervalEnum().name());
        budgetDTO.setStartDate(budget.getStartDate());
        return budgetDTO;
    }


    @Transactional
    public void resetBudgets() {
        List<Budget> budgets = budgetRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Budget budget : budgets) {
            if (shouldResetBudget(budget, today)) {
                log.info("Resetting budget {} for user {}", budget.getLabel(), budget.getUser().getId());
                budget.setAmount(budget.getInitialAmount());
                budget.setLastResetDate(today);
                budgetRepository.save(budget);
            }
        }
    }

    private boolean shouldResetBudget(Budget budget, LocalDate today) {
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
