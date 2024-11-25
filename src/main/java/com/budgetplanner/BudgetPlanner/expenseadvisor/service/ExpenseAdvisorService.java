package com.budgetplanner.BudgetPlanner.expenseadvisor.service;

import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.budget.repository.BudgetRepository;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expense.repository.ExpenseRepository;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetRecommendationResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.CategoriesDTO;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.ExpenseStatsDTO;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseAdvisorService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    private static final int DAILY_MIN_BUDGET = 10000;


    //todo 배치로 어제까지를 계산해서 저장해놓은 다음 활용하는게 훨씬 좋다고 일단은 생각중
    //오늘 지출 추천 - 응답
    @Cacheable(value = "expense", key = "'recommend:' + #authentication.name")
    public BudgetRecommendationResponse getRecommendation(Authentication authentication) {

        String account = authentication.getName();
        return gerRecommendationForUser(account);
    }

    // 오늘 지출 추천 - 웹훅 알림
    public BudgetRecommendationResponse getRecommendation(User user) {

        return gerRecommendationForUser(user.getAccount());
    }

    private BudgetRecommendationResponse gerRecommendationForUser(String account) {
        YearMonth yearMonth = YearMonth.now();
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDateTime startOfMonth = LocalDateTime.of(today.withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).with(LocalTime.MAX);

        // 1. 예산 데이터 가져오기
        List<Budget> budgets = budgetRepository.findBudgetsByAccountAndYearMonth(account, yearMonth);

        // 2. 예산 총합 계산
        long totalBudget = budgets.stream()
                .mapToLong(Budget::getBudget)
                .sum();

        // 3. 남은 일수 계산
        int remainingDays = (int) ChronoUnit.DAYS.between(today, lastDayOfMonth);

        // 4. 지출 데이터 가져오기
        List<Expense> expenses = expenseRepository.findExpensesByAccountAndPeriod(account, startOfMonth, yesterday);

        // 5. 카테고리별 지출 금액 계산 (카테고리별로 분리된 지출 금액을 계산)
        Map<Category, Long> categorySpent = expenses.stream()
                .filter(expense -> !expense.isExcludeTotalExpenses())  // 제외된 항목 제외
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingLong(Expense::getExpenses)
                ));

        // 6. 남은 예산 계산 (각 카테고리별로 남은 예산 계산)
        Map<Category, Long> categoryRemainingBudgets = new HashMap<>();
        for (Budget budget : budgets) {
            long spent = categorySpent.getOrDefault(budget.getCategory(), 0L);
            long remaining = budget.getBudget() - spent;
            categoryRemainingBudgets.put(budget.getCategory(), remaining);
        }

        // 7. 하루 예산 계산 (남은 예산 / 남은 일수)
        long totalRemainingBudget = categoryRemainingBudgets.values().stream().mapToLong(Long::longValue).sum();
        int dailyAmount = (int) Math.floor((double) totalRemainingBudget / remainingDays / 100) * 100;

        // 8. 댓글 로직 (예산과 지출에 따른 코멘트 제공)
        String comment = (dailyAmount < DAILY_MIN_BUDGET) ?
                "이번 달 소비가 많습니다. 오늘은 절약하시는 것을 추천드립니다! 화이팅!" :
                "이번 달 소비 계획이 잘 지켜지고 있습니다!";

        // 9. 카테고리별 예산 비율 계산
        Map<Category, Double> categoryRatios = budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getCategory,
                        budget -> (double) budget.getBudget() / totalBudget
                ));

        // 10. 카테고리별 예산 분배 (남은 예산을 각 카테고리 예산 비율에 맞게 분배)
        Map<Category, Integer> categoryBudgets = new HashMap<>();
        for (Budget budget : budgets) {
            double ratio = categoryRatios.get(budget.getCategory());

            // 각 카테고리의 남은 예산을 비율에 맞게 분배
            long categoryRemainingBudget = categoryRemainingBudgets.get(budget.getCategory());
            categoryBudgets.put(budget.getCategory(), (int) Math.floor(categoryRemainingBudget / 100.0) * 100);
        }

        // 11. 결과 반환
        return BudgetRecommendationResponse.builder()
                .dailyAmount(dailyAmount)
                .categoryBudgets(categoryBudgets)
                .comment(comment)
                .build();
    }

    // 오늘 지출 안내 - 응답
    @Cacheable(value = "expense", key = "'guide:' + #authentication.name")
    public ExpenseStatsDTO getGuide(Authentication authentication) {
        String account = authentication.getName();
        return getGuideResponse(account);
    }

    //오늘 지출 안내 - 웹훅 알림
    public ExpenseStatsDTO getGuide(User user) {
        return getGuideResponse(user.getAccount());
    }

    private ExpenseStatsDTO getGuideResponse(String account) {
        YearMonth yearMonth = YearMonth.now();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = LocalDateTime.of(today.withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime now = LocalDateTime.now();

        // 이번달 지출 조회
        List<Expense> expenses = expenseRepository.findExpensesByAccountAndPeriod(account, startOfMonth, now);

        //오늘 지출만 필터링
        List<Expense> todayExpenses = expenses.stream()
                .filter(expense -> expense.getSpendingTime().toLocalDate().equals(today))
                .collect(Collectors.toList());

        //카테고리별 오늘 지출
        Map<String, Long> categoryExpenses = todayExpenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().toString(),
                        Collectors.summingLong(expense -> Math.round(expense.getExpenses() / 100.0) * 100)
                ));

        // 이번달 예산
        List<Budget> budgets = budgetRepository.findBudgetsByAccountAndYearMonth(account, yearMonth);
        //카테고리별 예산
        Map<String, Long> categoryBudgets = budgets.stream()
                .collect(Collectors.groupingBy(
                        budget -> budget.getCategory().toString(),
                        Collectors.summingLong(budget -> Math.round(budget.getBudget() / 100.0) * 100)  // 백원 단위 반올림
                ));

        // 적정 금액, 지출 금액, 위험도 계산
        Map<String, CategoriesDTO> categoryStats = categoryBudgets.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String category = entry.getKey();
                            long budget = entry.getValue();
                            long expense = categoryExpenses.getOrDefault(category, 0L);

                            long idealAmount = budget / 30L;
                            long risk = (idealAmount == 0) ? 0 : Math.round((double) expense / idealAmount * 100);

                            return CategoriesDTO.builder()
                                    .idealAmount(idealAmount)
                                    .expenseAmount(expense)
                                    .risk(risk)
                                    .build();
                        }
                ));

        // 카테고리별 총 지출 금액 계산
        long totalExpense = categoryExpenses.values().stream().mapToLong(Long::longValue).sum();

        return ExpenseStatsDTO.builder()
                .totalExpense(totalExpense)
                .categoryTotals(categoryExpenses)
                .categoryStats(categoryStats)
                .build();
    }


    @CacheEvict(value = "expense", key = "'recommend:' + #authentication.name")
    @Scheduled(cron = "0 59 09 * * ?")
    public void recommendDataReset() {
    }

    @CacheEvict(value = "expense", key = "'guide:' + #authentication.name")
    @Scheduled(cron = "0 59 21 * * ?")
    public void guideDataReset() {
    }

}