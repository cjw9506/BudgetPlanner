package com.budgetplanner.BudgetPlanner.expense.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateExpenseRequest {

    @NotNull(message = "지출 비용은 필수입니다.")
    private Long expenses;

    @NotNull(message = "카테고리 지정은 필수입니다.")
    private Category category;

    private String memo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull(message = "지출 시간은 필수입니다.")
    private LocalDateTime spendingTime;
}
