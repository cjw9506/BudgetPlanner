package com.budgetplanner.BudgetPlanner.token.repository;

import com.budgetplanner.BudgetPlanner.token.entity.RefreshToken;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private final Cache<String, Long> refreshTokenCache = Caffeine.newBuilder()
            .expireAfterWrite(7, TimeUnit.DAYS)
            .maximumSize(100000)
            .build();

    public void save(final RefreshToken refreshToken) {
        refreshTokenCache.put(refreshToken.getRefreshToken(), refreshToken.getMemberId());
    }

    public boolean findById(final String refreshToken) {
        return refreshTokenCache.getIfPresent(refreshToken) != null;
    }

    public void delete(final String refreshToken) {

        refreshTokenCache.invalidate(refreshToken);
    }
}
