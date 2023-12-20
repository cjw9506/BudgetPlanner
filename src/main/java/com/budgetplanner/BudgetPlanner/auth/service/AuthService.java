package com.budgetplanner.BudgetPlanner.auth.service;

import com.budgetplanner.BudgetPlanner.auth.dto.AuthenticationResponse;
import com.budgetplanner.BudgetPlanner.auth.dto.UserLoginRequest;
import com.budgetplanner.BudgetPlanner.auth.dto.UserSignupRequest;
import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.token.entity.RefreshToken;
import com.budgetplanner.BudgetPlanner.token.repository.RefreshTokenRepository;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public void userSignup(UserSignupRequest request) {

        if (userRepository.findByAccount(request.getAccount()).isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }

        User user = User.builder()
                .account(request.getAccount())
                .password(passwordEncoder.encode(request.getPassword()))
                .webhookUrl(request.getWebhookUrl())
                .build();

        userRepository.save(user);
    }

    public AuthenticationResponse userLogin(UserLoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getAccount(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByAccount(request.getAccount()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        RefreshToken RT = RefreshToken.builder()
                .refreshToken(refreshToken)
                .memberId(user.getId())
                .build();

        refreshTokenRepository.save(RT);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
