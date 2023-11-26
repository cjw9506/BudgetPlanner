package com.budgetplanner.BudgetPlanner.budget.controller;

import com.budgetplanner.BudgetPlanner.budget.dto.BudgetRecommendRequest;
import com.budgetplanner.BudgetPlanner.budget.dto.BudgetRecommendResponse;
import com.budgetplanner.BudgetPlanner.budget.dto.BudgetSettingsRequest;
import com.budgetplanner.BudgetPlanner.budget.dto.CategoriesResponse;
import com.budgetplanner.BudgetPlanner.budget.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "budget", description = "예산 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "카테고리 목록", description = "카테고리 목록")
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {

        List<CategoriesResponse> categories = budgetService.getCategories();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @Operation(summary = "예산 설정", description = "예산 설정")
    @PostMapping
    public ResponseEntity<?> budgetSettings(@Valid @RequestBody BudgetSettingsRequest request,
                                            Authentication authentication) {

        budgetService.setting(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @Operation(summary = "예산 설계", description = "예산 설계")
    @GetMapping
    public ResponseEntity<?> recommendBudgets(@RequestBody BudgetRecommendRequest request) {

        List<BudgetRecommendResponse> list = budgetService.recommend(request);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
