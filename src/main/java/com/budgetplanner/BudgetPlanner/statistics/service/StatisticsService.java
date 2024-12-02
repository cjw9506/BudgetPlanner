package com.budgetplanner.BudgetPlanner.statistics.service;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expense.repository.ExpenseRepository;
import com.budgetplanner.BudgetPlanner.statistics.dto.StatisticsResponse;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public StatisticsResponse getStatistics(String data, Authentication authentication) {

        //저번달 1일
        LocalDateTime firstDayOfLastMonth = LocalDateTime.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        //현재 날짜 - 1달
        LocalDateTime aMonthAgo = LocalDateTime.now().minusMonths(1);
        //이번달 1일
        LocalDateTime firstDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        //현재
        LocalDateTime now = LocalDateTime.now();
        //저번달 1일
        LocalDateTime aWeekAgoStart = LocalDateTime.now().minusDays(7).with(LocalTime.MIN);
        //현재 날짜 - 7일
        LocalDateTime aWeekAgoEnd = LocalDateTime.now().minusDays(7).with(LocalTime.MAX);
        //오늘 자정
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);

        switch (data) {
            case "last-month":
                return handleLastMonthStatistics(authentication.getName(), firstDayOfLastMonth, aMonthAgo, firstDayOfMonth, now);
            case "last-week":
                return handleLastWeekStatistics(authentication.getName(), aWeekAgoStart, aWeekAgoEnd, todayStart, now);
            case "other-user":
                return handleOtherUserStatistics(authentication.getName(), firstDayOfMonth, now);
            default:
                throw new CustomException(ErrorCode.DATA_MIS_MATCH);
        }
    }

    private StatisticsResponse handleLastMonthStatistics(String username, LocalDateTime firstDayOfLastMonth, LocalDateTime aMonthAgo,
                                                         LocalDateTime firstDayOfMonth, LocalDateTime now) {
        List<Expense> expenses = expenseRepository.findExpensesByAccountAndPeriod(username, firstDayOfLastMonth, now);

        int lastMonthTotalSpent = calculateTotalSpent(expenses, firstDayOfLastMonth, aMonthAgo);
        int thisMonthTotalSpent = calculateTotalSpent(expenses, firstDayOfMonth, now);
        int compareTotalPercent = calculatePercent(thisMonthTotalSpent, lastMonthTotalSpent);

        Map<Category, String> compareCategoryPercent = calculateCategoryPercent(expenses, firstDayOfLastMonth, aMonthAgo, firstDayOfMonth, now);


        return StatisticsResponse.builder()
                .compareTotalPercent(compareTotalPercent)
                .compareCategoryPercent(compareCategoryPercent)
                .build();
    }

    private StatisticsResponse handleLastWeekStatistics(String username, LocalDateTime aWeekAgoStart, LocalDateTime aWeekAgoEnd,
                                                        LocalDateTime todayStart, LocalDateTime now) {
        int aWeekAgoTotalSpent = calculateTotalSpent(expenseRepository.findExpensesByAccountAndPeriod(username, aWeekAgoStart, aWeekAgoEnd));
        int todayTotalSpent = calculateTotalSpent(expenseRepository.findExpensesByAccountAndPeriod(username, todayStart, now));

        int compareTodayTotalPercent = calculatePercent(todayTotalSpent, aWeekAgoTotalSpent);

        return StatisticsResponse.builder()
                .compareTotalPercent(compareTodayTotalPercent)
                .build();
    }

    private StatisticsResponse handleOtherUserStatistics(String username, LocalDateTime firstDayOfMonth, LocalDateTime now) {
        List<Expense> expenses = expenseRepository.findBySpendingTimeBetween(firstDayOfMonth, now);

        long ownTotalSpent = calculateUserTotalExpenses(expenses, username);

        Map<User, Long> otherUsersExpenses = calculateOtherUsersExpenses(expenses, username);

        // 다른 사용자들의 평균 지출 계산
        double averageSpentByOthers = calculateAverage(otherUsersExpenses.values());

        // 자신의 지출 대비 평균 비율 계산
        int compareAverage = calculatePercentage(ownTotalSpent, averageSpentByOthers);

        // 결과 반환
        return StatisticsResponse.builder()
                .compareTotalPercent(compareAverage)
                .build();
    }

    private int calculateTotalSpent(List<Expense> expenses, LocalDateTime start, LocalDateTime end) {
        return (int) expenses.stream()
                .filter(expense -> !expense.getSpendingTime().isBefore(start) && !expense.getSpendingTime().isAfter(end))
                .filter(expense -> !expense.isExcludeTotalExpenses())
                .mapToLong(Expense::getExpenses)
                .sum();
    }

    private int calculateTotalSpent(List<Expense> expenses) {
        return (int) expenses.stream()
                .filter(expense -> !expense.isExcludeTotalExpenses())
                .mapToLong(Expense::getExpenses)
                .sum();
    }

    private int calculatePercent(int part, long total) {
        return total == 0 ? 0 : (int) ((double) part / total * 100);
    }

    private Map<Category, String> calculateCategoryPercent(List<Expense> expenses, LocalDateTime lastStart, LocalDateTime lastEnd,
                                                           LocalDateTime thisStart, LocalDateTime thisEnd) {
        Map<Category, Long> lastMonthCategoryTotal = calculateCategoryTotal(expenses, lastStart, lastEnd);
        Map<Category, Long> thisMonthCategoryTotal = calculateCategoryTotal(expenses, thisStart, thisEnd);

        Map<Category, String> compareCategoryPercent = new HashMap<>();
        lastMonthCategoryTotal.forEach((category, lastTotal) -> {
            long thisTotal = thisMonthCategoryTotal.getOrDefault(category, 0L);
            int percent = calculatePercent((int) thisTotal, lastTotal);
            compareCategoryPercent.put(category, String.format("%d%%", percent));
        });

        return compareCategoryPercent;
    }

    private Map<Category, Long> calculateCategoryTotal(List<Expense> expenses, LocalDateTime start, LocalDateTime end) {
        return expenses.stream()
                .filter(expense -> !expense.getSpendingTime().isBefore(start) && !expense.getSpendingTime().isAfter(end))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingLong(Expense::getExpenses)
                ));
    }
    private long calculateUserTotalExpenses(List<Expense> expenses, String username) {
        return expenses.stream()
                .filter(expense -> expense.getUser().getAccount().equals(username)) // 자신의 지출 필터링
                .filter(expense -> !expense.isExcludeTotalExpenses()) // 제외되지 않은 항목만 포함
                .mapToLong(Expense::getExpenses)
                .sum();
    }

    private Map<User, Long> calculateOtherUsersExpenses(List<Expense> expenses, String username) {
        return expenses.stream()
                .filter(expense -> !expense.getUser().getAccount().equals(username)) // 다른 사용자의 지출 필터링
                .collect(Collectors.groupingBy(
                        Expense::getUser,
                        Collectors.summingLong(Expense::getExpenses)
                ));
    }

    private double calculateAverage(Collection<Long> values) {
        if (values.isEmpty()) return 0;
        long total = values.stream().mapToLong(Long::longValue).sum();
        return (double) total / values.size();
    }

    private int calculatePercentage(double numerator, double denominator) {
        return denominator == 0 ? 0 : (int) ((numerator / denominator) * 100);
    }

}
