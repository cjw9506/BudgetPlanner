package com.budgetplanner.BudgetPlanner.auth.controller;

import com.budgetplanner.BudgetPlanner.auth.dto.UserLoginRequest;
import com.budgetplanner.BudgetPlanner.auth.dto.UserSignupRequest;
import com.budgetplanner.BudgetPlanner.auth.filter.JwtAuthenticationFilter;
import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
import com.budgetplanner.BudgetPlanner.auth.service.AuthService;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtils.class)
        })
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @DisplayName("회원가입 성공")
    @WithMockUser
    @Test
    void userSignup() throws Exception {
        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("test1234!@#$%")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/signup").with(csrf())
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(authService).userSignup(any(UserSignupRequest.class));
    }

    @DisplayName("회원가입 실패 - 계정 중복")
    @WithMockUser
    @Test
    void userSignupFail() throws Exception {

        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("test1234!@#$%")
                .build();

        doThrow(new CustomException(ErrorCode.USER_ALREADY_EXIST))
                .when(authService).userSignup(any(UserSignupRequest.class));


        String json = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/api/signup").with(csrf())
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService).userSignup(any(UserSignupRequest.class));
    }

    @DisplayName("로그인 테스트")
    @WithMockUser
    @Test
    void login() throws Exception {

        UserLoginRequest request = UserLoginRequest.builder()
                .account("testAccount")
                .password("password123!!")
                .build();

        String[] returnData = new String[]{"access-token", "refresh-token"};

        given(authService.userLogin(any(UserLoginRequest.class))).willReturn(returnData);

        mockMvc.perform(post("/api/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"Bearer\":\"access-token\"}")) //응답 액세스토큰
                .andExpect(cookie().value("Bearer", "refresh-token")); //쿠키 리프레시토큰
    }

    @DisplayName("새로운 액세스 토큰 발급")
    @WithMockUser
    @Test
    void issueRefreshToken() throws Exception{

        String refreshToken = "refresh-token";
        String newAccessToken = "new-access-token";

        given(authService.issueRefreshToken(refreshToken)).willReturn(newAccessToken);

        mockMvc.perform(post("/api/refresh").with(csrf())
                        .cookie(new Cookie("Bearer", refreshToken)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"Bearer\":\"new-access-token\"}"));
    }

}