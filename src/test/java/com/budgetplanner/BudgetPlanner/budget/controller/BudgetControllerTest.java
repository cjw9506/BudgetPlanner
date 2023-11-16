package com.budgetplanner.BudgetPlanner.budget.controller;

import com.budgetplanner.BudgetPlanner.auth.filter.JwtAuthenticationFilter;
import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
import com.budgetplanner.BudgetPlanner.budget.dto.BudgetRecommendRequest;
import com.budgetplanner.BudgetPlanner.budget.dto.BudgetSettingsRequest;
import com.budgetplanner.BudgetPlanner.budget.dto.CategoriesResponse;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.budget.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BudgetController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtils.class)
        })
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BudgetService budgetService;

    @DisplayName("카테고리 목록 조회")
    @WithMockUser
    @Test
    void showCategories() throws Exception {
        List<CategoriesResponse> expectedCategories = Arrays.asList(
                new CategoriesResponse("FOOD_EXPENSES", "식비"),
                new CategoriesResponse("TRANSPORTATION_EXPENSES", "교통비"),
                new CategoriesResponse("HOUSING_EXPENSES", "주거비"),
                new CategoriesResponse("SAVING_EXPENSES", "저축비"),
                new CategoriesResponse("ETC_EXPENSES", "기타비")
        );

        when(budgetService.getCategories()).thenReturn(expectedCategories);

        mockMvc.perform(get("/api/budgets/categories").with(csrf())
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(budgetService).getCategories();
    }

    @DisplayName("예산 설정")
    @WithMockUser
    @Test
    void budgetSetting() throws Exception {

        BudgetSettingsRequest request = BudgetSettingsRequest.builder()
                .categoryAndBudget(Arrays.asList(
                        new BudgetSettingsRequest.CreateCategoryAndBudget(Category.FOOD_EXPENSES, 200000L),
                        new BudgetSettingsRequest.CreateCategoryAndBudget(Category.TRANSPORTATION_EXPENSES, 150000L),
                        new BudgetSettingsRequest.CreateCategoryAndBudget(Category.HOUSING_EXPENSES, 150000L),
                        new BudgetSettingsRequest.CreateCategoryAndBudget(Category.SAVING_EXPENSES, 150000L),
                        new BudgetSettingsRequest.CreateCategoryAndBudget(Category.ETC_EXPENSES, 150000L)
                ))
                .yearMonth(YearMonth.parse("2012-12"))
                .build();
        Authentication authentication = mock(Authentication.class);

        String json = objectMapper.writeValueAsString(request);

        doNothing().when(budgetService).setting(request, authentication);

        mockMvc.perform(post("/api/budgets").with(csrf())
                .content(json)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(budgetService).setting(any(BudgetSettingsRequest.class), any(Authentication.class));
    }

}