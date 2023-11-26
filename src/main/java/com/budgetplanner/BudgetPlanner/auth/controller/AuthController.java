package com.budgetplanner.BudgetPlanner.auth.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

        String[] tokens = authService.userLogin(request);

        Map<String, String> response = new HashMap<>();

        response.put("Bearer", tokens[0]);

        ResponseCookie refreshToken = ResponseCookie.from("Bearer", tokens[1])
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .secure(true)
                .build();

        return ResponseEntity.ok().header("Set-Cookie", refreshToken.toString()).body(response);
    }
    @Operation(summary = "AT 재발급", description = "RT가 필요합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("Bearer") String refreshToken) {
        String newAccessToken = authService.issueRefreshToken(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("Bearer", newAccessToken);

        return ResponseEntity.ok().body(response);
    }
}
