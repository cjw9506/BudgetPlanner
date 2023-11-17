package com.budgetplanner.BudgetPlanner.expense.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ResultExpensesResponse {

    private Long totalExpenses;
    private Map<Category, Long> categoryExpenses;
    private List<GetExpensesResponse> expenses;

    @Builder
    public ResultExpensesResponse(Long totalExpenses, Map<Category, Long> categoryExpenses, List<GetExpensesResponse> expenses) {
        this.totalExpenses = totalExpenses;
        this.categoryExpenses = categoryExpenses;
        this.expenses = expenses;
    }
}
