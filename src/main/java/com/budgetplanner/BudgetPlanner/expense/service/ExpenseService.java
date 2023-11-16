package com.budgetplanner.BudgetPlanner.expense.service;

import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.expense.dto.CreateExpenseRequest;
import com.budgetplanner.BudgetPlanner.expense.dto.GetExpenseResponse;
import com.budgetplanner.BudgetPlanner.expense.dto.GetExpensesResponse;
import com.budgetplanner.BudgetPlanner.expense.dto.UpdateExpenseRequest;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expense.repository.ExpenseRepository;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        Expense expense = expenseRepository.findById(id)
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

    private void matchUser(Authentication authentication, Expense expense) {
        if (!expense.getUser().getAccount().equals(authentication.getName())) {
            throw new CustomException(ErrorCode.EXPENSE_USER_MISMATCH);
        }
    }

    public List<GetExpensesResponse> getExpenses(Authentication authentication) {

        User user = userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return expenseRepository.findByUser(user).stream()
                .map(GetExpensesResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, Authentication authentication, UpdateExpenseRequest request) {

        userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));


        expense.update(request.getSpendingTime(), request.getExpenses(),
                request.getCategory(), request.getMemo(), request.isExcludeTotalExpenses());
    }


    @Transactional
    public void delete(Long id, Authentication authentication) {
        userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        expenseRepository.deleteById(id);
    }
}
