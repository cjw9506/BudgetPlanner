package com.budgetplanner.BudgetPlanner.token.repository;

import com.budgetplanner.BudgetPlanner.token.entity.ExpiredToken;
import com.budgetplanner.BudgetPlanner.token.entity.ExpiredTokenCacheEntry;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class ExpiredTokenRepository {

    private final Cache<String, ExpiredTokenCacheEntry> expiredTokenCache = Caffeine.newBuilder()
            .maximumSize(100000)
            .build();

    public void save(final ExpiredToken expiredToken, long time) {
        Instant expirationTime = Instant.now().plusSeconds(time);

        expiredTokenCache.put(expiredToken.getExpiredToken(),
                new ExpiredTokenCacheEntry(expiredToken.getMemberId(), expirationTime));

    }

    public boolean isBlackList(final String expiredToken) {
        ExpiredTokenCacheEntry cacheEntry = expiredTokenCache.getIfPresent(expiredToken);

        if (cacheEntry == null) {
            return false;
        }

        if (cacheEntry.getExpirationTime().isBefore(Instant.now())) {
            expiredTokenCache.invalidate(expiredToken);
            return false;
        }
        return true;
    }

}
