package com.budgetplanner.BudgetPlanner.expenseadvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
public class CategoriesDTO {

    private long idealAmount;    // 일자기준 적정 금액
    private long expenseAmount;  // 지출 금액
    private long risk;           // 위험도 (적정 금액과 지출 금액의 차이를 퍼센트로 표시)

    @Builder
    public CategoriesDTO(long idealAmount, long expenseAmount, long risk) {
        this.idealAmount = idealAmount;
        this.expenseAmount = expenseAmount;
        this.risk = risk;
    }
}
