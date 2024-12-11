package com.budgetplanner.BudgetPlanner.token.entity;

import java.time.Instant;

public class ExpiredTokenCacheEntry {
    private final Long memberId;
    private final Instant expirationTime;

    public ExpiredTokenCacheEntry(Long memberId, Instant expirationTime) {
        this.memberId = memberId;
        this.expirationTime = expirationTime;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }
}
