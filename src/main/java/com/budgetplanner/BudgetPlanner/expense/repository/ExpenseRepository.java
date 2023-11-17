package com.budgetplanner.BudgetPlanner.expense.repository;

import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findBySpendingTimeBetweenAndUser(LocalDateTime startTime, LocalDateTime endTime, User user);
}

