package com.budgetplanner.BudgetPlanner.statistics.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class StatisticsResponse {

    private Integer compareTotalPercent;
    private Map<Category, String> compareCategoryPercent;

    @Builder
    public StatisticsResponse(Integer compareTotalPercent,
                              Map<Category, String> compareCategoryPercent) {
        this.compareTotalPercent = compareTotalPercent;
        this.compareCategoryPercent = compareCategoryPercent;
    }
}
