package com.budgetplanner.BudgetPlanner.expense.repository;

import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
