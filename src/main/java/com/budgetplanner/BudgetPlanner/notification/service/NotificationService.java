package com.budgetplanner.BudgetPlanner.notification.service;

import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetGuideResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.dto.BudgetRecommendationResponse;
import com.budgetplanner.BudgetPlanner.expenseadvisor.service.ExpenseAdvisorService;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final ExpenseAdvisorService expenseAdvisorService;
    private final ObjectMapper objectMapper;

    private static final int DAILY_MIN_BUDGET = 10000;

    public void sendRecommendMessages() throws JsonProcessingException {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            BudgetRecommendationResponse response = expenseAdvisorService.getRecommendationWebhook(user);
            sendUserBudgetRecommendMessage(user, response.getDailyAmount(),
                    response.getCategoryBudgets(), response.getComment());
        }

    }

    public void sendGuideMessages() throws JsonProcessingException {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            BudgetGuideResponse response = expenseAdvisorService.getGuideWebhook(user);

            sendUserBudgetGuideMessage(user, response.getTodaySpentAmount(),
                    response.getTodayCategorySpent(), response.getCategoryBudgets(),
                    response.getRisk());
        }
    }

    private void sendUserBudgetRecommendMessage(User user, int dailyAmount
            , Map<Category, Integer> categoryBudgets, String comment) throws JsonProcessingException {

        BudgetRecommendationResponse response = BudgetRecommendationResponse.builder()
                .dailyAmount(dailyAmount)
                .categoryBudgets(categoryBudgets)
                .comment(comment)
                .build();

        WebClient client = WebClient.builder()
                .baseUrl(user.getWebhookUrl())
                .build();

        String discordMessage = "```\n" + response.toString() + "\n```";

        String jsonContent = objectMapper.writeValueAsString(Map.of("content", discordMessage));

        client.post()
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(jsonContent))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private void sendUserBudgetGuideMessage(User user, int todaySpentAmount,
                                            Map<Category, Integer> todayCategorySpent,
                                            Map<Category, Integer> categoryBudgets,
                                            Map<Category, String> riskByCategory) throws JsonProcessingException {

        BudgetGuideResponse response = BudgetGuideResponse.builder()
                .todaySpentAmount(todaySpentAmount)
                .todayCategorySpent(todayCategorySpent)
                .categoryBudgets(categoryBudgets)
                .risk(riskByCategory)
                .build();

        WebClient client = WebClient.builder()
                .baseUrl(user.getWebhookUrl())
                .build();

        String discordMessage = "```\n" + response.toString() + "\n```";

        String jsonContent = objectMapper.writeValueAsString(Map.of("content", discordMessage));


        client.post()
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(jsonContent))
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

}
