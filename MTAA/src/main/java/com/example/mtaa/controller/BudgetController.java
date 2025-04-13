package com.example.mtaa.controller;

import com.example.mtaa.dto.BudgetDTO;
import com.example.mtaa.model.Budget;
import com.example.mtaa.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

    @Operation(summary = "Retrieves all existing budgets.", description = "Queries the database for a complete list of all existing budget objects.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all budgets.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("")
    public List<Budget> getAllBudgets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return budgetService.getAllBudgets(username);
    }

    @Operation(summary = "Retrieves budget by ID", description = "Queries the databse for a budget object based on ID from the user input.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public Budget getBudgetById(@PathVariable Long id) {
        return budgetService.getBudgetById(id);
    }

    @Operation(summary = "Creates a budget", description = "Creates and saves a new Budget object into the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity<Budget> addBudget(@RequestBody @Validated BudgetDTO budget) {
        return ResponseEntity.ok(budgetService.addBudget(budget));
    }

    @Operation(summary = "Updates a budget", description = "Updates and saves a Budget object in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody @Validated BudgetDTO budgetInput) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetInput));
    }

    @Operation(summary = "Deletes a budget", description = "Deletes a Budget object in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Budget> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resets all budgets that are due to reset based on their reset interval", description = "Based on the budgets reset interval(DAY/WEEK/MONTH/YEAR/NONE), all budgets that are supposed to reset their currency value have their value reset to the initial value the budget was created with.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @Async
    @GetMapping("/resetBudgets")
    public void resetBudgets(){
        budgetService.resetBudgets();
    }
}
