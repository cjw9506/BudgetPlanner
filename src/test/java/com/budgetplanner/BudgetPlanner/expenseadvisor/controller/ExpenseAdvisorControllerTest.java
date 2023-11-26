package com.budgetplanner.BudgetPlanner.expenseadvisor.controller;

import com.budgetplanner.BudgetPlanner.auth.filter.JwtAuthenticationFilter;
import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.expense.controller.ExpenseController;
import com.budgetplanner.BudgetPlanner.expense.dto.CreateExpenseRequest;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetRecommendationResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.service.ExpenseAdvisorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseAdvisorController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtils.class)
        })
class ExpenseAdvisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseAdvisorService expenseAdvisorService;

    @DisplayName("오늘 지출 추천")
    @WithMockUser
    @Test
    void createExpenseRecommend() throws Exception {

        Map<Category, Integer> categoryBudgets = new HashMap<>();

        categoryBudgets.put(Category.FOOD_EXPENSES, 100000);
        categoryBudgets.put(Category.HOUSING_EXPENSES, 100000);
        categoryBudgets.put(Category.TRANSPORTATION_EXPENSES, 100000);
        categoryBudgets.put(Category.SAVING_EXPENSES, 100000);
        categoryBudgets.put(Category.ETC_EXPENSES, 100000);

        BudgetRecommendationResponse response = BudgetRecommendationResponse.builder()
                .dailyAmount(100000)
                .comment("test")
                .categoryBudgets(categoryBudgets)
                .build();

        String json = objectMapper.writeValueAsString(response);

        when(expenseAdvisorService.getRecommendation(any())).thenReturn(response);

        mockMvc.perform(get("/api/expense-advisor/recommend").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }
}