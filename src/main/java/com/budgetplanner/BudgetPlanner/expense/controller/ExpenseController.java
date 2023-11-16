package com.budgetplanner.BudgetPlanner.expense.controller;

import com.budgetplanner.BudgetPlanner.expense.dto.CreateExpenseRequest;
import com.budgetplanner.BudgetPlanner.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
