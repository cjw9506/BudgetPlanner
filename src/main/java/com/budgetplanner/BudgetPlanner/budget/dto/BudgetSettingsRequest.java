package com.budgetplanner.BudgetPlanner.budget.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.YearMonth;
import java.util.List;

@Getter
public class BudgetSettingsRequest {

    @NotNull(message = "카테고리와 예산은 필수입니다.")
    private List<CreateCategoryAndBudget> categoryAndBudget;

    @NotNull(message = "년, 월은 필수입니다.")
    private YearMonth yearMonth;

    @Getter
    public static class CreateCategoryAndBudget {
        private Category category;
        private Long budget;
    }
}
