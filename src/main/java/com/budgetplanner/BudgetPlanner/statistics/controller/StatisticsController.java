package com.budgetplanner.BudgetPlanner.statistics.controller;

import com.budgetplanner.BudgetPlanner.statistics.dto.StatisticsResponse;
import com.budgetplanner.BudgetPlanner.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<?> getStatistics(@RequestParam("data") String data,
                                           Authentication authentication) {
        StatisticsResponse response = statisticsService.getStatistics(data, authentication);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
