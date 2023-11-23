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

    //todo 이번달, 저번달 지출이 없을 시 처리해줘야함 + 리팩토링 필수(지저분, 일단 되게만)
    public StatisticsResponse getStatistics(String data, Authentication authentication) {

        User user = userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        switch (data) {
            case "last-month":
                /*
                * 지난달 지출 총액 가져오기
                * */
                //저번달 1일
                LocalDateTime firstDayOfLastMonth = LocalDateTime.now().minusMonths(1)
                        .with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
                //현재 날짜 - 1달
                LocalDateTime aMonthAgo = LocalDateTime.now().minusMonths(1);

                List<Expense> lastMonthExpenses = expenseRepository.findBySpendingTimeBetweenAndUser(
                        firstDayOfLastMonth, aMonthAgo, user);

                //저번달 지출 총액
                Integer lastMonthTotalSpent = (int) lastMonthExpenses.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();
                /*
                * 이번달 지출 총액 가져오기
                * */

                //이번달 1일
                LocalDateTime firstDayOfMonth = LocalDateTime.now()
                        .with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);

                //현재
                LocalDateTime now = LocalDateTime.now();

                List<Expense> thisMonthExpenses = expenseRepository.findBySpendingTimeBetweenAndUser(
                        firstDayOfMonth, now, user);

                //이번달 지출 총액
                Integer thisMonthTotalSpent = (int) thisMonthExpenses.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();

                //저번달 대비 이번달 소비율
                int compareTotalPercent = (int) ((double) thisMonthTotalSpent / lastMonthTotalSpent * 100);

                //지난달 카테고리 별 지출 총액 가져오기
                Map<Category, Long> lastMonthCategoryTotalSpent = lastMonthExpenses.stream()
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

                //비교해서 퍼센티지
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
                //저번달 1일
                LocalDateTime aWeekAgoStart = LocalDateTime.now().minusDays(7).with(LocalTime.MIN);
                //현재 날짜 - 1달
                LocalDateTime aWeekAgoEnd = LocalDateTime.now().minusDays(7).with(LocalTime.MAX);

                List<Expense> aWeekAgoExpenses = expenseRepository.findBySpendingTimeBetweenAndUser(
                        aWeekAgoStart, aWeekAgoEnd, user);

                //일주일 전(하루) 지출
                Integer aWeekAgoTotalSpent = (int) aWeekAgoExpenses.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();
                //오늘 하루 지출 총액가져오기

                LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
                LocalDateTime todayNow = LocalDateTime.now();

                List<Expense> todayExpenses = expenseRepository.findBySpendingTimeBetweenAndUser(todayStart, todayNow, user);

                Integer todayTotalSpent = (int) todayExpenses.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .mapToLong(Expense::getExpenses)
                        .sum();

                //비교해서 퍼센티지
                int compareTodayTotalPercent = (int) ((double) todayTotalSpent / aWeekAgoTotalSpent * 100);

                return StatisticsResponse.builder()
                        .compareTotalPercent(compareTodayTotalPercent)
                        .build();

            case "other-user":
                //다른 유저들의 이번달 지출 가져와서 평균내기
                //이번달 1일
                LocalDateTime start = LocalDateTime.now()
                        .with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
                //현재
                LocalDateTime end = LocalDateTime.now();

                List<Expense> findUser = expenseRepository.findBySpendingTimeBetweenAndUser(start, end, user);

                Long findUserTotalSpent = findUser.stream()
                        .filter(expense -> !expense.isExcludeTotalExpenses())
                        .collect(Collectors.summingLong(Expense::getExpenses));

                List<Expense> users = expenseRepository.findBySpendingTimeBetween(start, end);

                Map<User, Long> usersExpenses = users.stream()
                        .collect(Collectors.groupingBy(
                                Expense::getUser,
                                Collectors.summingLong(Expense::getExpenses)
                        ));

                Long totalSpent = 0L;
                for (Long value : usersExpenses.values()) {
                    totalSpent += value;
                }

                double average = ((double) totalSpent / usersExpenses.size());

                int compareAverage = (int)((double) findUserTotalSpent / average * 100);

                return StatisticsResponse.builder()
                        .compareTotalPercent(compareAverage)
                        .build();

        }
        throw new IllegalArgumentException(); //todo 추후 변경
    }
}
