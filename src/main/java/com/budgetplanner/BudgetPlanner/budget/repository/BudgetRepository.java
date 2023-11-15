package com.budgetplanner.BudgetPlanner.budget.repository;

import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
