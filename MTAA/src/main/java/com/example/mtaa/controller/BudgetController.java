package com.example.mtaa.controller;

import com.example.mtaa.dto.BudgetDTO;
import com.example.mtaa.model.Budget;
import com.example.mtaa.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("")
    public List<Budget> getAllBudgets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return budgetService.getAllBudgets(username);
    }

    @GetMapping("/{id}")
    public Budget getBudgetById(@PathVariable Integer id) {
        return budgetService.getBudgetById(id);
    }

    @PostMapping("")
    public ResponseEntity<Budget> addBudget(@RequestBody @Validated BudgetDTO budget) {
        return ResponseEntity.ok(budgetService.addBudget(budget));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Integer id, @RequestBody @Validated BudgetDTO budgetInput) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetInput));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Budget> deleteBudget(@PathVariable Integer id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok().build();
    }
}
