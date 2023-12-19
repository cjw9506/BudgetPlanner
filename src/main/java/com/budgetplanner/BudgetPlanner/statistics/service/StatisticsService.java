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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        LocalDateTime aWeekAgoStart =LocalDateTime.now().minusDays(7).with(LocalTime.MIN);
        //현재 날짜 - 7일
        LocalDateTime aWeekAgoEnd = LocalDateTime.now().minusDays(7).with(LocalTime.MAX);
        //오늘 자정
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);

        User user = userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        switch (data) {
            case "last-month":

                List<Expense> fromLastMonthToTodayExpenses = expenseRepository.findBySpendingTimeBetweenAndUser(
                        firstDayOfLastMonth, now, user);

                //저번달 지출 총액
                Integer lastMonthTotalSpent = (int) fromLastMonthToTodayExpenses.stream()
                        .filter(expense -> !expense.getSpendingTime().isBefore(firstDayOfLastMonth) &&
                                !expense.getSpendingTime().isAfter(aMonthAgo))
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();

                //이번달 지출
                List<Expense> thisMonthExpenses = fromLastMonthToTodayExpenses.stream()
                        .filter(expense -> !expense.getSpendingTime().isBefore(firstDayOfMonth) &&
                                !expense.getSpendingTime().isAfter(now))
                        .collect(Collectors.toList());

                //이번달 지출 총액
                Integer thisMonthTotalSpent = (int) thisMonthExpenses.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();

                //저번달 대비 이번달 소비율
                int compareTotalPercent = (int) ((double) thisMonthTotalSpent / lastMonthTotalSpent * 100);

                //지난달 카테고리 별 지출 총액 가져오기
                Map<Category, Long> lastMonthCategoryTotalSpent = fromLastMonthToTodayExpenses.stream()
                        .collect(Collectors.groupingBy(
                                Expense::getCategory,
                                LinkedHashMap::new, //순서보장
                                Collectors.summingLong(Expense::getExpenses)
                        ));

                //이번달 카테고리 별 지출 총액 가져오기
                Map<Category, Long> thisMonthCategoryTotalSpent = thisMonthExpenses.stream()
                        .collect(Collectors.groupingBy(
                                Expense::getCategory,
                                LinkedHashMap::new, //순서보장
                                Collectors.summingLong(Expense::getExpenses)
                        ));

                //비율
                Map<Category, String> compareCategoryPercent = new HashMap<>();

                lastMonthCategoryTotalSpent.forEach((category, lastMonthTotal) -> {
                    // 이번 달의 카테고리별 총 지출
                    long thisMonthTotal = thisMonthCategoryTotalSpent.getOrDefault(category, 0L);

                    // 저번 달 대비 이번 달 지출 비율 계산
                    int ratio = (int) ((double) thisMonthTotal / lastMonthTotal * 100);

                    // 결과 맵에 추가
                    compareCategoryPercent.put(category, String.format("%d%%", ratio));
                });

                return StatisticsResponse.builder()
                        .compareTotalPercent(compareTotalPercent)
                        .compareCategoryPercent(compareCategoryPercent)
                        .build();

            case "last-week":

                //-7일 전 하루 지출 총액가져오기
                List<Expense> aWeekAgoExpenses = expenseRepository.findBySpendingTimeBetweenAndUser(
                        aWeekAgoStart, aWeekAgoEnd, user);

                //일주일 전(하루) 지출
                Integer aWeekAgoTotalSpent = (int) aWeekAgoExpenses.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();

                //오늘 하루 지출 총액가져오기
                List<Expense> todayExpenses = expenseRepository.findBySpendingTimeBetweenAndUser(todayStart, now, user);

                Integer todayTotalSpent = (int) todayExpenses.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();

                //비율
                int compareTodayTotalPercent = (int) ((double) todayTotalSpent / aWeekAgoTotalSpent * 100);

                return StatisticsResponse.builder()
                        .compareTotalPercent(compareTodayTotalPercent)
                        .build();

            case "other-user":

                //모든 유저의 지출
                List<Expense> usersExpenses = expenseRepository.findBySpendingTimeBetween(firstDayOfMonth, now);

                //자신의 지출
                List<Expense> userExpense = usersExpenses.stream()
                        .filter(expense -> expense.getUser().equals(user))
                        .collect(Collectors.toList());

                //자신의 총 지출
                Long ownTotalSpent = userExpense.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .collect(Collectors.summingLong(Expense::getExpenses));

                //자신 제외 모든 유저지출
                Map<User, Long> otherUsersExpenses = usersExpenses.stream()
                        .filter(expense -> !expense.getUser().equals(user))
                        .collect(Collectors.groupingBy(
                                Expense::getUser,
                                Collectors.summingLong(Expense::getExpenses)
                        ));

                Long totalSpent = 0L;
                for (Long value : otherUsersExpenses.values()) {
                    totalSpent += value;
                }

                double average = ((double) totalSpent / otherUsersExpenses.size());
                int compareAverage = (int)((double) ownTotalSpent / average * 100);

                return StatisticsResponse.builder()
                        .compareTotalPercent(compareAverage)
                        .build();

        }
        throw new CustomException(ErrorCode.DATA_MIS_MATCH);
    }
}
