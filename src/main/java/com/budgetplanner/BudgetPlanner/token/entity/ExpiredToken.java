package com.budgetplanner.BudgetPlanner.token.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
public class ExpiredToken {

    @Id
    private String expiredToken;

    private Long memberId;

    @Builder
    public ExpiredToken(String expiredToken, Long memberId) {
        this.expiredToken = expiredToken;
        this.memberId = memberId;
    }
}
