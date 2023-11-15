package com.budgetplanner.BudgetPlanner.budget.repository;

import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Query("SELECT b.category, SUM(b.budget) FROM Budget b GROUP BY b.category")
    List<Object[]> findCategoryAndBudget();
}
