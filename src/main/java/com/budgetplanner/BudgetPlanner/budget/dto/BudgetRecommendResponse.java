package com.budgetplanner.BudgetPlanner.budget.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Getter;

@Getter
public class BudgetRecommendResponse {

    private Category category;
    private Long budget;

    public BudgetRecommendResponse(Category category, Long budget) {
        this.category = category;
        this.budget = budget;
    }
}
