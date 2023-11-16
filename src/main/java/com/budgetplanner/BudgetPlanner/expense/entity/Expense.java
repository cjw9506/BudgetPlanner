package com.budgetplanner.BudgetPlanner.expense.entity;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import jakarta.persistence.*;
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



}
