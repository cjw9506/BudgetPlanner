package com.budgetplanner.BudgetPlanner.expense.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetExpensesResponse {

    private Long id;
    private Category category;
    private Long expenses;
    private LocalDateTime spendingTime;
    private String memo;
    private boolean excludeTotalExpenses;

    public GetExpensesResponse(Expense expense) {
        this.id = expense.getId();
        this.category = expense.getCategory();
        this.expenses = expense.getExpenses();
        this.spendingTime = expense.getSpendingTime();
        this.memo = expense.getMemo();
        this.excludeTotalExpenses = expense.isExcludeTotalExpenses();
    }

    @Builder
    public GetExpensesResponse(Long id, Category category, Long expenses, LocalDateTime spendingTime, String memo, boolean excludeTotalExpenses) {
        this.id = id;
        this.category = category;
        this.expenses = expenses;
        this.spendingTime = spendingTime;
        this.memo = memo;
        this.excludeTotalExpenses = excludeTotalExpenses;
    }
}
