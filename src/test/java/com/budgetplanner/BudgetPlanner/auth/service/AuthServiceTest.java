package com.budgetplanner.BudgetPlanner.auth.service;

import com.budgetplanner.BudgetPlanner.auth.dto.UserLoginRequest;
import com.budgetplanner.BudgetPlanner.auth.dto.UserSignupRequest;
import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
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

    @Mock
    private JwtUtils jwtUtils;

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

    @DisplayName("로그인 성공")
    @Test
    void login_Success() {
        // Arrange
        String account = "testAccount";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String[] tokens = new String[]{"access-token", "refresh-token"};

        UserLoginRequest request = UserLoginRequest.builder()
                .account(account)
                .password(password)
                .build();

        User user = User.builder()
                .account(account)
                .password(encodedPassword)
                .build();

        //given
        when(userRepository.findByAccount(account)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtils.generateToken(account)).thenReturn(tokens);

        //when
        String[] result = authService.userLogin(request);

        //then
        assertEquals(tokens[0], result[0]);
        assertEquals(tokens[1], result[1]);
    }

    @DisplayName("로그인 시 유저 못찾음")
    @Test
    void login_UserNotFound() {
        String account = "testAccount";
        String password = "password123";

        UserLoginRequest request = UserLoginRequest.builder()
                .account(account)
                .password(password)
                .build();

        //given
        when(userRepository.findByAccount(account)).thenReturn(Optional.empty());

        //then
        assertThrows(CustomException.class, () -> authService.userLogin(request));
    }

    @DisplayName("로그인시 패스워드 일치하지 않음")
    @Test
    void login_PasswordMismatch() {
        String account = "testAccount";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        UserLoginRequest request = UserLoginRequest.builder()
                .account(account)
                .password(password)
                .build();

        User user = User.builder()
                .account(account)
                .password(encodedPassword)
                .build();

        //given
        when(userRepository.findByAccount(account)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        //then
        assertThrows(CustomException.class, () -> authService.userLogin(request));
    }

    @DisplayName("토큰 재발급")
    @Test
    void issueNewAccessToken() {

        String refreshToken = "refresh-token";
        String newAccessToken = "new-access-token";

        when(jwtUtils.verifyRefreshTokenAndReissue(refreshToken)).thenReturn(newAccessToken);

        String result = authService.issueRefreshToken(refreshToken);

        assertEquals(newAccessToken, result);
    }
}