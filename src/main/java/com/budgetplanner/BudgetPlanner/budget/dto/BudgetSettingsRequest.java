package com.budgetplanner.BudgetPlanner.budget.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Getter
@NoArgsConstructor
public class BudgetSettingsRequest {

    @NotNull(message = "카테고리와 예산은 필수입니다.")
    private List<CreateCategoryAndBudget> categoryAndBudget;

    @NotNull(message = "년, 월은 필수입니다.")
    private YearMonth yearMonth;

    @Getter
    @NoArgsConstructor
    public static class CreateCategoryAndBudget {
        private Category category;
        private Long budget;

        public CreateCategoryAndBudget(Category category, Long budget) {
            this.category = category;
            this.budget = budget;
        }
    }

    @Builder
    public BudgetSettingsRequest(List<CreateCategoryAndBudget> categoryAndBudget, YearMonth yearMonth) {
        this.categoryAndBudget = categoryAndBudget;
        this.yearMonth = yearMonth;
    }
}
