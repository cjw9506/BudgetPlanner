package com.budgetplanner.BudgetPlanner.expenseadvisor.controller;

import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetGuideResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetRecommendationResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.service.ExpenseAdvisorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지출 컨설팅", description = "지출 컨설팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense-advisor")
public class ExpenseAdvisorController {

    private final ExpenseAdvisorService expenseAdvisorService;

    @Operation(summary = "오늘 지출 추천", description = "오늘 지출 추천")
    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(Authentication authentication) {

        BudgetRecommendationResponse response = expenseAdvisorService.getRecommendation(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "오늘 지출 안내", description = "오늘 지출 안내")
    @GetMapping("/guide")
    public ResponseEntity<?> guide(Authentication authentication) {

        BudgetGuideResponse response = expenseAdvisorService.getGuide(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
