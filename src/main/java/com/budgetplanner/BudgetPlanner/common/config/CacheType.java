package com.budgetplanner.BudgetPlanner.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    BUDGET("budget", 30, 100000),
    EXPENSE("expense", 30, 100000);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
