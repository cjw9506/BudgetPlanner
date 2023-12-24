package com.budgetplanner.BudgetPlanner.budget.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoriesResponse {

    private String categoryCode;
    private String categoryName;

    public CategoriesResponse(String categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }
}
