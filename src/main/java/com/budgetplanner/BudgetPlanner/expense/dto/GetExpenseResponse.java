package com.budgetplanner.BudgetPlanner.expense.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetExpenseResponse {

    private Long id;
    private Long userId;
    private Category category;
    private Long expenses;
    private LocalDateTime spendingTime;
    private String memo;
    private boolean excludeTotalExpenses;

    @Builder
    public GetExpenseResponse(Long id, Long userId, Category category, Long expenses
            , LocalDateTime spendingTime, String memo, boolean excludeTotalExpenses) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.expenses = expenses;
        this.spendingTime = spendingTime;
        this.memo = memo;
        this.excludeTotalExpenses = excludeTotalExpenses;
    }
}
