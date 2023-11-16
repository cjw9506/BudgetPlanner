package com.budgetplanner.BudgetPlanner.expense.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateExpenseRequest {

    @NotNull(message = "지출 비용은 필수입니다.")
    private Long expenses;

    @NotNull(message = "카테고리 지정은 필수입니다.")
    private Category category;

    private String memo;
}
