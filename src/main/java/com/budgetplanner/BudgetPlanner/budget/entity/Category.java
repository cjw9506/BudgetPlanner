package com.budgetplanner.BudgetPlanner.budget.entity;

import lombok.Getter;

@Getter
public enum Category{

    FOOD_EXPENSES("식비"),
    TRANSPORTATION_EXPENSES("교통비"),
    HOUSING_EXPENSES("주거비"),
    SAVING_EXPENSES("저축비"),
    ETC_EXPENSES("기타비"),
    ;

    private final String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }

}
