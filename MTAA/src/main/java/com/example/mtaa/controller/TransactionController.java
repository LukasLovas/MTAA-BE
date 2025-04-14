package com.example.mtaa.controller;

import com.example.mtaa.dto.CategorySpendingDTO;
import com.example.mtaa.dto.TransactionDTO;
import com.example.mtaa.model.Transaction;
import com.example.mtaa.service.CurrencyAPIService;
import com.example.mtaa.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrencyAPIService currencyAPIService;

    public TransactionController(TransactionService transactionService, CurrencyAPIService currencyAPIService) {
        this.transactionService = transactionService;
        this.currencyAPIService = currencyAPIService;
    }

    @Operation(summary = "Retrieves all Transaction objects.", description = "Queries the database for a complete list of all existing Transaction objects.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("")
    public List<Transaction> getAllTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getAllTransactions(username);
    }

    @Operation(summary = "Retrieves all expenses made today.", description = "Queries the database for a all Transaction object based on their date being in a days interval from today.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/expenses/today")
    public List<CategorySpendingDTO> getExpensesToday() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getExpensesToday(username);
    }
    @Operation(summary = "Retrieves all expenses made this week.", description = "Queries the database for a all Transaction object based on their date being in a weeks interval from today.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/expenses/week")
    public List<CategorySpendingDTO> getExpensesWeek() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getExpensesForWeek(username);
    }
    @Operation(summary = "Retrieves all expenses made this month.", description = "Queries the database for a all Transaction object based on their date being in a days interval from today.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/expenses/month")
    public List<CategorySpendingDTO> getExpensesMonth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return transactionService.getExpensesForMonth(username);
    }

    @Operation(summary = "Creates a Transaction", description = "Creates and saves a new Transaction object into the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity<Transaction> addTransaction(@RequestBody @Validated TransactionDTO transactionInput) {
        return ResponseEntity.ok(transactionService.addTransaction(currencyAPIService.convertCurrency(transactionInput)));
    }

    @Operation(summary = "Retrieves Transaction by ID", description = "Queries the database for a transaction object based on ID from the user input.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @Operation(summary = "Retrieves Transaction by ID of the budget the transaction is attached to.", description = "Queries the database for a budget object based on budget ID from the user input.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/budget/{budgetId}")
    public List<Transaction> getTransactionsByBudgetId(@PathVariable Long budgetId) {
        return transactionService.getTransactionsByBudgetId(budgetId);
    }

    @Operation(summary = "Updates a Transaction", description = "Updates and saves a Transaction object in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody @Validated TransactionDTO transactionInput) {
        transactionInput = currencyAPIService.convertCurrency(transactionInput);

        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionInput));
    }

    @Operation(summary = "Deletes a Transaction", description = "Deletes a Transaction object in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction with this id was not found.", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not permitted to access this endpoint.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Transaction> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
