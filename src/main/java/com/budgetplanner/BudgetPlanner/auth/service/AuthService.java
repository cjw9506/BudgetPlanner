package com.budgetplanner.BudgetPlanner.auth.service;

import com.budgetplanner.BudgetPlanner.auth.dto.UserLoginRequest;
import com.budgetplanner.BudgetPlanner.auth.dto.UserSignupRequest;
import com.budgetplanner.BudgetPlanner.auth.jwt.JwtUtils;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    @Transactional
    public void userSignup(UserSignupRequest request) {

        if (userRepository.findByAccount(request.getAccount()).isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .account(request.getAccount())
                .password(encodedPassword)
                .build();

        userRepository.save(user);
    }

    public String[] userLogin(UserLoginRequest request) {

        userRepository.findByAccount(request.getAccount()).
                filter(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return true;
                    } else {
                        throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
                    }
                })
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return jwtUtils.generateToken(request.getAccount());
    }

    public String issueRefreshToken(String refreshToken) {

        return jwtUtils.verifyRefreshTokenAndReissue(refreshToken);
    }
}
