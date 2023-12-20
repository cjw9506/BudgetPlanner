package com.budgetplanner.BudgetPlanner.auth.controller;

import com.budgetplanner.BudgetPlanner.auth.dto.AccessTokenResponse;
import com.budgetplanner.BudgetPlanner.auth.dto.AuthenticationResponse;
import com.budgetplanner.BudgetPlanner.auth.dto.UserLoginRequest;
import com.budgetplanner.BudgetPlanner.auth.dto.UserSignupRequest;
import com.budgetplanner.BudgetPlanner.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequest request) {

        authService.userSignup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {

        AuthenticationResponse tokens = authService.userLogin(request);

        AccessTokenResponse response = AccessTokenResponse.builder()
                .token(tokens.getAccessToken())
                .build();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie", cookie.toString())
                .body(response);

    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue("refreshToken") String refreshToken,
                                     Authentication authentication) {

        AccessTokenResponse response = authService.reissue(refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
