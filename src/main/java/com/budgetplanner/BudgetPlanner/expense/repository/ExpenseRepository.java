package com.budgetplanner.BudgetPlanner.expense.repository;

import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findBySpendingTimeBetweenAndUser(LocalDateTime startTime, LocalDateTime endTime, User user);

    List<Expense> findBySpendingTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Query("select e from Expense e join fetch e.user where e.id = :id")
    Optional<Expense> findByIdWithUser(@Param("id") Long id);
}
