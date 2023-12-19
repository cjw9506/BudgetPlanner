package com.budgetplanner.BudgetPlanner.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;

    @Builder
    public AuthenticationResponse(String token) {
        this.token = token;
    }
}
