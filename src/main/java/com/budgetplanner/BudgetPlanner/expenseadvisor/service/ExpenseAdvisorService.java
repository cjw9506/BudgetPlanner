package com.budgetplanner.BudgetPlanner.expenseadvisor.service;

import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.budget.repository.BudgetRepository;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expense.repository.ExpenseRepository;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetGuideResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetRecommendationResponse;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
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

    //todo 코트 리팩토링 --> 필수
    //todo 밑에 guide 오늘 카테고리별 사용 금액 double -> integer, risk %붙여서 스트링으로 바꾸기!!!

    /*
     * 오늘 지출 추천
     * */
    public BudgetRecommendationResponse getRecommendation(Authentication authentication) {

        User user = getUser(authentication);
        List<Budget> budgets = getBudgetsForUser(user.getId());
        int remainingDays = calculateRemainingDays(); //남은 일수 계산
        long budget = getBudgets(budgets); //유저의 설정된 예산
        long spentAmount = amountUsedThisMonth(user); //유저가 이제까지 쓴 금액
        int dailyAmount = calculateDailyAmount(remainingDays, budget, spentAmount); //오늘 지출 가능한 금액

        comment = (dailyAmount < DAILY_MIN_BUDGET) ?
                "이번 달 소비가 많습니다. 오늘은 절약하시는 것을 추천드립니다! 화이팅!" :
                "이번 달 소비 계획이 잘 지켜지고 있습니다!";

        //유저 카테고리별 예산 비율 구하기
        Map<Category, Double> categoryRatios = calculateCategoryRatios(budgets, budget); //유저 예산 비율 구하기
        //카테고리별 사용 가능한 유저 예산
        Map<Category, Integer> categoryBudgets = getCategoryBudgets(budgets, dailyAmount, categoryRatios);

        return BudgetRecommendationResponse.builder()
                .dailyAmount(dailyAmount)
                .categoryBudgets(categoryBudgets)
                .comment(comment)
                .build();

    }
    /*
    * 오늘 지출 안내
    * */
    public BudgetGuideResponse getGuide(Authentication authentication) {

        User user = getUser(authentication);
        List<Expense> expenses = getExpensesToday(user);
        int todaySpentAmount = calculateTodaySpentAmount(expenses); //오늘 지출한 총액
        Map<Category, Integer> todayCategorySpent = calculateTodayCategorySpent(expenses); //오늘 카테고리 별 사용한 금액

        List<Budget> budgets = getBudgetsForUser(user.getId());
        int remainingDays = calculateRemainingDays(); //남은 일수 계산
        long budget = getBudgets(budgets); //유저의 설정된 예산
        long spentAmount = amountUsedThisMonth(user); //유저가 이제까지 쓴 금액
        int dailyAmount = calculateDailyAmount(remainingDays, budget, spentAmount); //오늘 지출 가능한 금액

        //유저 카테고리별 예산 비율 구하기
        Map<Category, Double> categoryRatios = calculateCategoryRatios(budgets, budget);
        //카테고리별 사용 가능한 유저 예산
        Map<Category, Integer> categoryBudgets = getCategoryBudgets(budgets, dailyAmount, categoryRatios);
        //오늘 카테고리 별 위험도
        Map<Category, String> riskByCategory = riskByCategory(todayCategorySpent, categoryBudgets);

        return BudgetGuideResponse.builder()
                .todaySpentAmount(todaySpentAmount)
                .todayCategorySpent(todayCategorySpent)
                .categoryBudgets(categoryBudgets)
                .risk(riskByCategory)
                .build();


    }

    public List<Budget> getBudgetsForUser(Long userId) {
        return budgetRepository.findAllByUserIdAndYearMonth(userId, YearMonth.now());
    }

    public Map<Category, Integer> calculateTodayCategorySpent(List<Expense> expenses) {
        Map<Category, Long> todayCategorySpent = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingLong(Expense::getExpenses)
                ));

        Map<Category, Integer> todayCategorySpentAsInteger = todayCategorySpent.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().intValue()
                ));

        return todayCategorySpentAsInteger;
    }


    public int calculateTodaySpentAmount(List<Expense> expenses) {
        int todaySpentAmount = (int) expenses.stream()
                .filter(expense -> !expense.isExcludeTotalExpenses())
                .mapToLong(Expense::getExpenses)
                .sum();
        return todaySpentAmount;
    }

    public List<Expense> getExpensesToday(User user) {
        List<Expense> expenses = expenseRepository.findBySpendingTimeBetweenAndUser(
                LocalDateTime.now().with(LocalTime.MIN), LocalDateTime.now().with(LocalTime.MAX), user);
        return expenses;
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public Map<Category, Integer> getCategoryBudgets(List<Budget> budgets, int dailyAmount, Map<Category, Double> categoryRatios) {
        return budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getCategory,
                        b -> (int) (Math.floor(dailyAmount * categoryRatios.get(b.getCategory()) / 100) * 100)
                ));
    }

    public Map<Category, Double> calculateCategoryRatios(List<Budget> budgets, long budget) {
        Map<Category, Double> categoryRatios = budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getCategory,
                        b -> (double) b.getBudget() / budget
                ));
        return categoryRatios;
    }

    public long getBudgets(List<Budget> budgets) {
        long budget = budgets.stream()
                .mapToLong(Budget::getBudget)
                .sum();
        return budget;
    }

    public long amountUsedThisMonth(User user) {
        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).with(LocalTime.MAX);


        long spentAmount = expenseRepository.findBySpendingTimeBetweenAndUser(startOfMonth, yesterday, user).stream()
                .filter(expense -> !expense.isExcludeTotalExpenses())
                .mapToLong(Expense::getExpenses)
                .sum();
        return spentAmount;
    }

    public int calculateRemainingDays() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        long remainingDays = ChronoUnit.DAYS.between(today, lastDayOfMonth);

        return (int) remainingDays;
    }

    public int calculateDailyAmount(int remainingDays, long budget, long spentAmount) {
        return (int) Math.floor((double) (budget - spentAmount) / remainingDays / 100) * 100;
    }

    public Map<Category, String> riskByCategory(Map<Category, Integer> todayCategorySpent, Map<Category, Integer> categoryBudgets) {
        Map<Category, String> riskByCategory = todayCategorySpent.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            int spent = entry.getValue();
                            int todayBudget = categoryBudgets.getOrDefault(entry.getKey(), 0);

                            if (todayBudget == 0) {
                                // 예산이 0이면 위험도를 정의할 수 없음
                                return "0%";
                            }

                            // 위험도 계산
                            int riskPercentage = Math.round((float) spent / todayBudget * 100);
                            return riskPercentage + "%";
                        }
                ));
        return riskByCategory;
    }



}
