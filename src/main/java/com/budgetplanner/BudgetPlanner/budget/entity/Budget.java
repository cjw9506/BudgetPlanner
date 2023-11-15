package com.budgetplanner.BudgetPlanner.budget.entity;

import com.budgetplanner.BudgetPlanner.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Entity
@Getter
@NoArgsConstructor
public class Budget {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //예산
    private Long budget;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "yyyy_mm")
    private YearMonth yearMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Budget(Long budget, Category category, YearMonth yearMonth, User user) {
        this.budget = budget;
        this.category = category;
        this.yearMonth = yearMonth;
        this.user = user;
    }
}
