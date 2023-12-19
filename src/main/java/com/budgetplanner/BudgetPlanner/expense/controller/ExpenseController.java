package com.budgetplanner.BudgetPlanner.expense.controller;

import com.budgetplanner.BudgetPlanner.expense.dto.*;
import com.budgetplanner.BudgetPlanner.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "지출", description = "지출 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "지출 생성", description = "지출 생성")
    @PostMapping
    public ResponseEntity<?> createExpense(@Valid @RequestBody CreateExpenseRequest request,
                                           Authentication authentication) {

        expenseService.create(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @Operation(summary = "지출 단건 조회", description = "지출 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpense(@PathVariable Long id, Authentication authentication) {

        GetExpenseResponse response = expenseService.getExpense(id, authentication);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "지출 목록 조회", description = "지출 목록 조회")
    @GetMapping
    public ResponseEntity<?> getExpenses(Authentication authentication,
                                         ParamsRequest request) {

        ResultExpensesResponse response = expenseService.getExpenses(authentication, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "지출 수정", description = "지출 수정")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id,
                                           Authentication authentication,
                                           @RequestBody UpdateExpenseRequest request) {

        expenseService.update(id, authentication, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @Operation(summary = "지출 삭제", description = "지출 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id,
                                           Authentication authentication) {
        expenseService.delete(id, authentication);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @Operation(summary = "지출 제외", description = "지출 제외")
    @PatchMapping("/{id}/exclude")
    public ResponseEntity<?> excludeExpense(@PathVariable Long id,
                                            Authentication authentication) {
        expenseService.exclude(id, authentication);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
