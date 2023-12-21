package com.budgetplanner.BudgetPlanner.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenResponse {

    private String token;

    @Builder
    public AccessTokenResponse(String token) {
        this.token = token;
    }
}
