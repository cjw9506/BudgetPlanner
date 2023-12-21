package com.budgetplanner.BudgetPlanner.token.repository;

import com.budgetplanner.BudgetPlanner.token.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate redisTemplate;

    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getMemberId());
        redisTemplate.expire(refreshToken.getRefreshToken(), 60 * 60 * 24 * 7, TimeUnit.SECONDS);
    }

    public boolean findById(final String refreshToken) {
        return redisTemplate.hasKey(refreshToken);

    }

    public void delete(final String refreshToken) {
        redisTemplate.delete(refreshToken);
    }
}
