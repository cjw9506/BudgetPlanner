package com.budgetplanner.BudgetPlanner.expenseadvisor.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class BudgetGuideResponse {

    private int todaySpentAmount;
    private Map<Category, Long> todayCategorySpent;
    private Map<Category, Integer> categoryBudgets;
    private Map<Category, String> risk;

    @Builder
    public BudgetGuideResponse(int todaySpentAmount, Map<Category, Long> todayCategorySpent,
                               Map<Category, Integer> categoryBudgets, Map<Category, String> risk) {
        this.todaySpentAmount = todaySpentAmount;
        this.todayCategorySpent = todayCategorySpent;
        this.categoryBudgets = categoryBudgets;
        this.risk = risk;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("💰 **예산 관리 알림** 💰\n");
        result.append("오늘의 지출: ").append(todaySpentAmount).append("원\n");
        result.append("카테고리별 지출:\n");

        for (Map.Entry<Category, Long> entry : todayCategorySpent.entrySet()) {
            String categoryName = entry.getKey().name(); // 카테고리 이름
            long spentAmount = entry.getValue(); // 해당 카테고리의 지출 금액

            result.append("  • ").append(categoryName).append(": ").append(spentAmount).append("원\n");
        }

        result.append("\n예산 상태:\n");

        for (Map.Entry<Category, Integer> entry : categoryBudgets.entrySet()) {
            String categoryName = entry.getKey().name(); // 카테고리 이름
            int budgetAmount = entry.getValue(); // 해당 카테고리의 예산

            result.append("  • ").append(categoryName).append(" 예산: ").append(budgetAmount).append("원\n");
        }

        result.append("\n리스크 분석:\n");

        for (Map.Entry<Category, String> entry : risk.entrySet()) {
            String categoryName = entry.getKey().name(); // 카테고리 이름
            String riskPercentage = entry.getValue(); // 해당 카테고리의 리스크

            result.append("  • ").append(categoryName).append(" 리스크: ").append(riskPercentage).append("\n");
        }

        result.append("\nEnjoy your budgeting! 💸");

        return result.toString();
    }
}
