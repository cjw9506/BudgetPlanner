package com.budgetplanner.BudgetPlanner.expenseadvisor.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class BudgetGuideResponse {

    private int todaySpentAmount;
    private Map<Category, Double> todayCategorySpent;
    private Map<Category, Integer> categoryBudgets;
    private Map<Category, Integer> risk;

    @Builder
    public BudgetGuideResponse(int todaySpentAmount, Map<Category, Double> todayCategorySpent,
                               Map<Category, Integer> categoryBudgets, Map<Category, Integer> risk) {
        this.todaySpentAmount = todaySpentAmount;
        this.todayCategorySpent = todayCategorySpent;
        this.categoryBudgets = categoryBudgets;
        this.risk = risk;
    }
}
