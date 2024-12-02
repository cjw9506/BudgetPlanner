package com.budgetplanner.BudgetPlanner.expenseadvisor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class ExpenseStatsDTO {

    private Long totalExpense;
    private Map<String, Long> categoryTotals;
    private Map<String, CategoriesDTO> categoryStats;

    @Builder
    public ExpenseStatsDTO(Long totalExpense, Map<String, Long> categoryTotals, Map<String, CategoriesDTO> categoryStats) {
        this.totalExpense = totalExpense;
        this.categoryTotals = categoryTotals;
        this.categoryStats = categoryStats;
    }
}
