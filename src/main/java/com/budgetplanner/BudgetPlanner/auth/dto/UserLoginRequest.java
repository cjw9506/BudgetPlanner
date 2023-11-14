package com.budgetplanner.BudgetPlanner.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequest {

    @NotBlank(message = "계정은 필수입니다.")
    private String account;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    public UserLoginRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }
}
