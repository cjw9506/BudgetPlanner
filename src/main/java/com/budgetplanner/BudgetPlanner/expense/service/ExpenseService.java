package com.budgetplanner.BudgetPlanner.expense.service;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.expense.dto.*;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expense.repository.ExpenseRepository;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Transactional
    public void create(CreateExpenseRequest request, Authentication authentication) {

        User user = userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Expense expense = Expense.builder()
                .expenses(request.getExpenses())
                .category(request.getCategory())
                .spendingTime(request.getSpendingTime())
                .memo(request.getMemo())
                .user(user)
                .excludeTotalExpenses(false)
                .build();

        expenseRepository.save(expense);
    }

    public GetExpenseResponse getExpense(Long id, Authentication authentication) {

        Expense expense = expenseRepository.findByIdWithUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        matchUser(authentication, expense);

        GetExpenseResponse response = GetExpenseResponse.builder()
                .id(expense.getId())
                .userId(expense.getUser().getId())
                .category(expense.getCategory())
                .expenses(expense.getExpenses())
                .spendingTime(expense.getSpendingTime())
                .memo(expense.getMemo())
                .excludeTotalExpenses(expense.isExcludeTotalExpenses())
                .build();

        return response;
    }

    @Cacheable(value = "expense", key = "'list:' + #authentication.name")
    public ResultExpensesResponse getExpenses(Authentication authentication, ParamsRequest request) {
        User user = userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime startTime = request.start().atStartOfDay();
        LocalDateTime endTime = request.end().atTime(23, 59, 59);

        List<GetExpensesResponse> expenses = expenseRepository.findExpensesByAccountAndPeriod(authentication.getName(), startTime, endTime)
                .stream()
                .map(GetExpensesResponse::new)
                .filter(expense -> isCategoryMatch(request, expense) && isAmountInRange(request, expense))
                .collect(Collectors.toList());

        long totalExpenses = expenses.stream()
                .filter(expense -> !expense.isExcludeTotalExpenses())
                .mapToLong(GetExpensesResponse::getExpenses)
                .sum();

        Map<Category, Long> categoryExpenses = expenses.stream()
                .collect(Collectors.groupingBy(GetExpensesResponse::getCategory,
                        Collectors.summingLong(GetExpensesResponse::getExpenses)));

        return ResultExpensesResponse.builder()
                .totalExpenses(totalExpenses)
                .categoryExpenses(categoryExpenses)
                .expenses(expenses)
                .build();
    }

    @CacheEvict(value = "expense", key = "'list:' + #authentication.name")
    @Transactional
    public void update(Long id, Authentication authentication, UpdateExpenseRequest request) {

        userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        matchUser(authentication, expense);

        expense.update(request.getSpendingTime(), request.getExpenses(),
                request.getCategory(), request.getMemo());
    }

    @CacheEvict(value = "expense", key = "'list:' + #authentication.name")
    @Transactional
    public void delete(Long id, Authentication authentication) {
        userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        expenseRepository.deleteById(id);
    }


    @CacheEvict(value = "expense", key = "'list:' + #authentication.name")
    @Transactional
    public void exclude(Long id, Authentication authentication) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        matchUser(authentication, expense);

        expense.exclude();
    }

    private boolean isCategoryMatch(ParamsRequest request, GetExpensesResponse expense) {
        return request.category() == null || expense.getCategory() == request.category();
    }

    private boolean isAmountInRange(ParamsRequest request, GetExpensesResponse expense) {
        boolean minCondition = request.min() == null || expense.getExpenses() >= request.min();
        boolean maxCondition = request.max() == null || expense.getExpenses() <= request.max();
        return minCondition && maxCondition;
    }

    private void matchUser(Authentication authentication, Expense expense) {
        if (!expense.getUser().getAccount().equals(authentication.getName())) {
            throw new CustomException(ErrorCode.EXPENSE_USER_MISMATCH);
        }
    }

    public LocalDateTime generate() {
        Random random = new Random();

        int randomDays = random.nextInt(61) - 30; // -30 ~ +30 일 범위 (한 달 전부터 한 달 후까지)

        int randomHours = random.nextInt(24);  // 0~23 시간
        int randomMinutes = random.nextInt(60); // 0~59 분
        int randomSeconds = random.nextInt(60); // 0~59 초

        // 현재 날짜에서 randomDays 만큼 더하거나 빼기
        LocalDateTime randomDateTime = LocalDateTime.now()
                .plus(randomDays, ChronoUnit.DAYS)  // 랜덤 날짜 더하기
                .plus(randomHours, ChronoUnit.HOURS)  // 랜덤 시간 더하기
                .plus(randomMinutes, ChronoUnit.MINUTES)  // 랜덤 분 더하기
                .plus(randomSeconds, ChronoUnit.SECONDS);

        return randomDateTime;
    }

    @Transactional
    public void bulk() {

        List<Expense> list = new ArrayList<>();

        List<User> users = new ArrayList<>();

        for (int i = 1; i < 1000; i++) {
            User user = User.builder()
                    .account("dksktlqkf" + i)
                    .password("dksktlqkf")
                    .build();
            userRepository.save(user);
            users.add(user);
        }




        long startTime = System.currentTimeMillis();

        for (int i = 1; i < 100000; i++) {

            User user = users.get(i % users.size());
            Expense expense = Expense.builder()
                    .user(user)
                    .expenses(1000L)
                    .spendingTime(generate())
                    .category(Category.HOUSING_EXPENSES)
                    .memo("test")
                    .build();
            list.add(expense);
        }

        expenseRepository.saveAll(list);

        System.out.println("taken time = "+(System.currentTimeMillis() - startTime)+"ms");

    }

}
