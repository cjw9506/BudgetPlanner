package com.budgetplanner.BudgetPlanner.budget.controller;

import com.budgetplanner.BudgetPlanner.budget.dto.BudgetSettingsRequest;
import com.budgetplanner.BudgetPlanner.budget.dto.CategoriesResponse;
import com.budgetplanner.BudgetPlanner.budget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {

        List<CategoriesResponse> categories = budgetService.getCategories();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @PostMapping
    public ResponseEntity<?> budgetSettings(@Valid @RequestBody BudgetSettingsRequest request,
                                            Authentication authentication) {

        budgetService.setting(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

}
