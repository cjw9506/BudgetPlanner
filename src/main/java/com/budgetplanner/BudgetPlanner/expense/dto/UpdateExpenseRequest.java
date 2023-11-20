package com.budgetplanner.BudgetPlanner.expense.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateExpenseRequest {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime spendingTime;

    private Long expenses;
    private Category category;
    private String memo;

    @Builder
    public UpdateExpenseRequest(LocalDateTime spendingTime, Long expenses, Category category, String memo) {
        this.spendingTime = spendingTime;
        this.expenses = expenses;
        this.category = category;
        this.memo = memo;
    }
}
