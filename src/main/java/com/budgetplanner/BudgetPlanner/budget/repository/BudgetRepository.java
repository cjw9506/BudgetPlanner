package com.budgetplanner.BudgetPlanner.budget.repository;

import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.YearMonth;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Query("SELECT b FROM Budget b " +
           "WHERE b.user.account = :account " +
           "AND b.yearMonth = :yearMonth")
    List<Budget> findBudgetsByAccountAndYearMonth(@Param("account") String account,
                                                  @Param("yearMonth") YearMonth yearMonth);



    @Query("SELECT b.category, SUM(b.budget) FROM Budget b GROUP BY b.category")
    List<Object[]> findCategoryAndBudget();

    List<Budget> findAllByUserIdAndYearMonth(Long id, YearMonth date);
}
