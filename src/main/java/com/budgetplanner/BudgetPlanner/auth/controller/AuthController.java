package com.budgetplanner.BudgetPlanner.auth.controller;

import com.budgetplanner.BudgetPlanner.auth.dto.UserLoginRequest;
import com.budgetplanner.BudgetPlanner.auth.dto.UserSignupRequest;
import com.budgetplanner.BudgetPlanner.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequest request) {

        authService.userSignup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("Bearer") String refreshToken) {
        String newAccessToken = authService.issueRefreshToken(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("Bearer", newAccessToken);

        return ResponseEntity.ok().body(response);
    }
}
