package com.example.mtaa.service;

import com.example.mtaa.dto.BudgetDTO;
import com.example.mtaa.model.Budget;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.enums.IntervalEnum;
import com.example.mtaa.repository.BudgetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget addBudget(BudgetDTO input) {
        Budget budget = convertToBudget(input);
        return budgetRepository.save(budget);
    }

    public Budget getBudgetById(int budgetId) {
        return budgetRepository.findById(budgetId).orElseThrow(() ->
                new CommonException(HttpStatus.NOT_FOUND, "Budget not found"));
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

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    private Budget convertToBudget(BudgetDTO input) {
        Budget budget = new Budget();
        budget.setLabel(input.getLabel());
        budget.setAmount(input.getAmount());
        budget.setIntervalValue(input.getIntervalValue());
        budget.setIntervalEnum(IntervalEnum.valueOf(input.getIntervalEnum()));
        budget.setStartDate(input.getStartDate());
        return budget;
    }

    private BudgetDTO convertToBudgetDTO(Budget budget) {
        BudgetDTO budgetDTO = new BudgetDTO();
        budgetDTO.setLabel(budget.getLabel());
        budgetDTO.setAmount(budget.getAmount());
        budgetDTO.setIntervalValue(budget.getIntervalValue());
        budgetDTO.setIntervalEnum(budget.getIntervalEnum().name());
        budgetDTO.setStartDate(budget.getStartDate());
        return budgetDTO;
    }
}
