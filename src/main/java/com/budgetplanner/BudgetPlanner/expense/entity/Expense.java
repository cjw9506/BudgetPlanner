package com.budgetplanner.BudgetPlanner.expense.entity;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Expense {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //지출 일시
    private LocalDateTime spendingTime;

    private Long expenses;

    private Category category;

    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    //지출 합계 제외
    private boolean excludeTotalExpenses;

    @Builder
    public Expense(LocalDateTime spendingTime, Long expenses, Category category, String memo, User user,
                   boolean excludeTotalExpenses) {
        this.spendingTime = spendingTime;
        this.expenses = expenses;
        this.category = category;
        this.memo = memo;
        this.user = user;
        this.excludeTotalExpenses = excludeTotalExpenses;
    }

    public void update(LocalDateTime spendingTime, Long expenses
            , Category category, String memo) {
        this.spendingTime = spendingTime != null ? spendingTime : this.spendingTime;
        this.expenses = expenses != null ? expenses : this.expenses;
        this.category = category != null ? category : this.category;
        this.memo = memo != null ? memo : this.memo;
    }
}
