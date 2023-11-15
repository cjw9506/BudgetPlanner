package com.budgetplanner.BudgetPlanner.budget.service;

import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
import com.budgetplanner.BudgetPlanner.budget.dto.CategoriesResponse;
import com.budgetplanner.BudgetPlanner.budget.repository.BudgetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @InjectMocks
    private BudgetService budgetService;

    @Mock
    private BudgetRepository budgetRepository;


    @DisplayName("카테고리 목록 조회")
    @Test
    void showCategories() {

        List<CategoriesResponse> categories = budgetService.getCategories();

        assertEquals(5, categories.size());
        assertEquals(categories.get(0).getCategoryCode(), "FOOD_EXPENSES");
        assertEquals(categories.get(1).getCategoryName(), "교통비");

    }

}