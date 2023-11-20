package com.budgetplanner.BudgetPlanner.expenseadvisor.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class BudgetRecommendationResponse {

    private int dailyAmount;
    private Map<Category, Integer> categoryBudgets;
    private String comment;

    @Builder
    public BudgetRecommendationResponse(int dailyAmount, Map<Category, Integer> categoryBudgets,
                                        String comment) {
        this.dailyAmount = dailyAmount;
        this.categoryBudgets = categoryBudgets;
        this.comment = comment;
    }
}
