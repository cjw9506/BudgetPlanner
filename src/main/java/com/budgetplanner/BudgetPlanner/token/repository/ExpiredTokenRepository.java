package com.budgetplanner.BudgetPlanner.token.repository;

import com.budgetplanner.BudgetPlanner.token.entity.ExpiredToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ExpiredTokenRepository {

    private final RedisTemplate redisTemplate;

    public void save(final ExpiredToken expiredToken, long time) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(expiredToken.getExpiredToken(), expiredToken.getMemberId());
        redisTemplate.expire(expiredToken.getExpiredToken(), time, TimeUnit.SECONDS);
    }

}
