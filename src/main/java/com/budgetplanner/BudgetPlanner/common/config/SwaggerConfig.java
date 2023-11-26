package com.budgetplanner.BudgetPlanner.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI budgetPlannerAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Budget Planner API")
                        .description("Budget Planner API 명세서입니다.")
                        .version("v1.0.0"));
    }

}
