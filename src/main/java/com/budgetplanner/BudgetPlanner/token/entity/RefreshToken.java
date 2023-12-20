package com.budgetplanner.BudgetPlanner.token.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
public class RefreshToken {

    @Id
    private String refreshToken;

    private Long memberId;

    @Builder
    public RefreshToken(final String refreshToken, final Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

}
