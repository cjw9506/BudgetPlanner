package com.budgetplanner.BudgetPlanner.expenseadvisor.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class BudgetRecommendationResponse {

    private int dailyAmount;
    private Map<Category, Integer> categoryBudgets;
    private String comment;

    @Builder
    public BudgetRecommendationResponse(int dailyAmount, Map<Category, Integer> categoryBudgets,
                                        String comment) {
        this.dailyAmount = dailyAmount;
        this.categoryBudgets = categoryBudgets;
        this.comment = comment;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("💰 **예산 관리 알림** 💰\n");
        result.append("오늘 사용가능한 금액: ").append(dailyAmount).append("원\n");
        result.append("카테고리별 사용가능한 금액:\n");

        for (Map.Entry<Category, Integer> entry : categoryBudgets.entrySet()) {
            String categoryName = entry.getKey().name(); // 카테고리 이름
            int spentAmount = entry.getValue(); // 해당 카테고리의 사용가능한 금액

            result.append("  • ").append(categoryName).append(": ").append(spentAmount).append("원\n");
        }

        result.append(comment);
        result.append("\nEnjoy your budgeting! 💸");

        return result.toString();
    }
}
