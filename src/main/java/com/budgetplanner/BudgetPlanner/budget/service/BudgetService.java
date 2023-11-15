package com.budgetplanner.BudgetPlanner.budget.service;

import com.budgetplanner.BudgetPlanner.budget.dto.CategoriesResponse;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    public List<CategoriesResponse> getCategories() {

        return Arrays.stream(Category.values())
                .map(category -> new CategoriesResponse(category.name(), category.getCategoryName()))
                .collect(Collectors.toList());
    }
}
