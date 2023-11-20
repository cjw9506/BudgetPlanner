package com.budgetplanner.BudgetPlanner.expense.controller;

import com.budgetplanner.BudgetPlanner.auth.controller.AuthController;
import com.budgetplanner.BudgetPlanner.auth.filter.JwtAuthenticationFilter;
import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.expense.dto.*;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expense.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtils.class)
        })
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @DisplayName("지출 생성")
    @WithMockUser
    @Test
    void createExpense() throws Exception {

        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .expenses(100000L)
                .category(Category.HOUSING_EXPENSES)
                .memo("점심 값")
                .spendingTime(LocalDateTime.parse("2023-11-21 10:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/expense").with(csrf())
                .contentType(APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("지출 전체 조회")
    @WithMockUser
    @Test
    void getExpenses() throws Exception {

        long totalExpenses = 100000;

        GetExpensesResponse response = GetExpensesResponse.builder()
                .id(1L)
                .category(Category.HOUSING_EXPENSES)
                .expenses(100000L)
                .memo("저녁 값")
                .spendingTime(LocalDateTime.parse("2023-11-21 10:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .excludeTotalExpenses(false)
                .build();

        List<GetExpensesResponse> expenses = List.of(response);

        Map<Category, Long> categoryExpenses = expenses.stream()
                .collect(Collectors.groupingBy(GetExpensesResponse::getCategory,
                        Collectors.summingLong(GetExpensesResponse::getExpenses)));

        ResultExpensesResponse result = ResultExpensesResponse.builder()
                .totalExpenses(totalExpenses)
                .expenses(expenses)
                .categoryExpenses(categoryExpenses)
                .build();

        when(expenseService.getExpenses(any(), any())).thenReturn(result);

        mockMvc.perform(get("/api/expense?start=2023-01-01&end=2023-11-16").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }



    @DisplayName("지출 단건 조회")
    @WithMockUser
    @Test
    void getExpense() throws Exception {

        GetExpenseResponse response = GetExpenseResponse.builder()
                .id(1L)
                .userId(1L)
                .category(Category.HOUSING_EXPENSES)
                .expenses(100000L)
                .memo("저녁 값")
                .spendingTime(LocalDateTime.parse("2023-11-21 10:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .excludeTotalExpenses(false)
                .build();

        when(expenseService.getExpense(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/expense/{id}", 1).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("지출 변경")
    @WithMockUser
    @Test
    void updateExpense() throws Exception {

        UpdateExpenseRequest request = UpdateExpenseRequest.builder()
                .expenses(1000L)
                .category(Category.FOOD_EXPENSES)
                .memo("수정 저녁 값")
                .spendingTime(LocalDateTime.parse("2023-11-21 11:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();

        doNothing().when(expenseService).update(anyLong(), any(Authentication.class), any(UpdateExpenseRequest.class));

        mockMvc.perform(patch("/api/expense/{id}", 1)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("지출 삭제")
    @WithMockUser
    @Test
    void deleteExpense() throws Exception {

        doNothing().when(expenseService).delete(any(), any(Authentication.class));

        mockMvc.perform(delete("/api/expense/{id}", 1)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("지출 - 합계제외 상태 변경")
    @WithMockUser
    @Test
    void excludeExpense() throws Exception {

        doNothing().when(expenseService).exclude(any(), any(Authentication.class));

        mockMvc.perform(patch("/api/expense/{id}/exclude", 1)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

}