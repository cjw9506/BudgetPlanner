package com.budgetplanner.BudgetPlanner.expense.controller;

import com.budgetplanner.BudgetPlanner.expense.dto.*;
import com.budgetplanner.BudgetPlanner.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> createExpense(@Valid @RequestBody CreateExpenseRequest request,
                                           Authentication authentication) {

        expenseService.create(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpense(@PathVariable Long id, Authentication authentication) {

        GetExpenseResponse response = expenseService.getExpense(id, authentication);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getExpenses(Authentication authentication,
                                         ParamsRequest request) {

        ResultExpensesResponse response = expenseService.getExpenses(authentication, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id,
                                           Authentication authentication,
                                           @RequestBody UpdateExpenseRequest request) {

        expenseService.update(id, authentication, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id,
                                           Authentication authentication) {
        expenseService.delete(id, authentication);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PatchMapping("/{id}/exclude")
    public ResponseEntity<?> excludeExpense(@PathVariable Long id,
                                            Authentication authentication) {
        expenseService.exclude(id, authentication);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
