package com.budgetplanner.BudgetPlanner.expenseadvisor.service;

import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.budget.repository.BudgetRepository;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.expense.dto.GetExpensesResponse;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expense.repository.ExpenseRepository;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetRecommendationResponse;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseAdvisorService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    private static final int DAILY_MIN_BUDGET = 10000;
    private static String comment = "";


    public BudgetRecommendationResponse getRecommendation(Authentication authentication) {

        User user = userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Budget> budgets = budgetRepository.findByUser(user);

        //남은 일수 계산
        int remainingDays = calculateRemainingDays();

        //유저의 설정된 예산
        long budget = getBudgets(budgets);

        //유저가 이제까지 쓴 금액
        long spentAmount = amountUsedThisMonth(user);

        //오늘 지출 가능한 금액
        int dailyAmount = (int)Math.floor((double)(budget - spentAmount) / remainingDays / 100) * 100;

        if (dailyAmount < DAILY_MIN_BUDGET) {
            dailyAmount = DAILY_MIN_BUDGET;
            comment = "이번 달 소비가 많습니다. 오늘은 절약하시는 것을 추천드립니다! 화이팅!";
        } else {
            comment = "이번 달 소비 계획이 잘 지켜지고 있습니다!";
        }

        //유저 예산 비율 구하기
        Map<Category, Double> categoryRatios = calculateCategoryRatios(budgets, budget);

        //카테고리별 사용 가능한 유저 예산
        Map<Category, Integer> categoryBudgets = getCategoryBudgets(budgets, dailyAmount, categoryRatios);

        return BudgetRecommendationResponse.builder()
                .dailyAmount(dailyAmount)
                .categoryBudgets(categoryBudgets)
                .comment(comment)
                .build();

    }

    private Map<Category, Integer> getCategoryBudgets(List<Budget> budgets, int dailyAmount, Map<Category, Double> categoryRatios) {
        return budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getCategory,
                        b -> (int) (Math.floor(dailyAmount * categoryRatios.get(b.getCategory()) / 100) * 100)
                ));
    }

    private Map<Category, Double> calculateCategoryRatios(List<Budget> budgets, long budget) {
        Map<Category, Double> categoryRatios = budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getCategory,
                        b -> (double) b.getBudget() / budget
                ));
        return categoryRatios;
    }

    private long getBudgets(List<Budget> budgets) {
        long budget = budgets.stream()
                .mapToLong(Budget::getBudget)
                .sum();
        return budget;
    }

    private long amountUsedThisMonth(User user) {
        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).with(LocalTime.MAX);


        long spentAmount = expenseRepository.findBySpendingTimeBetweenAndUser(startOfMonth, yesterday, user).stream()
                .filter(expense -> !expense.isExcludeTotalExpenses())
                .mapToLong(Expense::getExpenses)
                .sum();
        return spentAmount;
    }

    private int calculateRemainingDays() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        long remainingDays = ChronoUnit.DAYS.between(today, lastDayOfMonth);

        return (int) remainingDays;
    }
}
