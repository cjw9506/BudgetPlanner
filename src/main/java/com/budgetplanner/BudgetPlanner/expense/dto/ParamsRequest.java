package com.budgetplanner.BudgetPlanner.expense.dto;

import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public record ParamsRequest(@RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate end,
                            @RequestParam(name = "category", required = false) Category category,
                            @RequestParam(name = "min", required = false) Long min,
                            @RequestParam(name = "max", required = false) Long max) {
}
