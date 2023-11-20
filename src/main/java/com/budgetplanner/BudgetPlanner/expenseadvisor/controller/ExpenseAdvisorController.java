package com.budgetplanner.BudgetPlanner.expenseadvisor.controller;

import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetGuideResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetRecommendationResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.service.ExpenseAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense-advisor")
public class ExpenseAdvisorController {

    private final ExpenseAdvisorService expenseAdvisorService;

    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(Authentication authentication) {

        BudgetRecommendationResponse response = expenseAdvisorService.getRecommendation(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/guide")
    public ResponseEntity<?> guide(Authentication authentication) {

        BudgetGuideResponse response = expenseAdvisorService.getGuide(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
