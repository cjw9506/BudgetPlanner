package com.budgetplanner.BudgetPlanner.auth.service;

import com.budgetplanner.BudgetPlanner.auth.dto.UserSignupRequest;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원가입 성공")
    @Test
    void signupSuccess() {

        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("test1234!@#$%")
                .build();

        String encodedPassword = "encodedPassword";

        when(userRepository.findByAccount(request.getAccount())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);

        authService.userSignup(request);

        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("회원가입 실패 - 계정 중복")
    @Test
    void signupFailedWithDuplicateAccount() {
        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("test1234!@#$%")
                .build();

        User user = User.builder()
                .account("testAccount")
                .password("testpassword12!!!")
                .build();


        when(userRepository.findByAccount(request.getAccount())).thenReturn(Optional.of(user));

        // 예외를 기대하는 테스트
        assertThrows(CustomException.class, () -> authService.userSignup(request));

        verify(userRepository, never()).save(any());
    }
}