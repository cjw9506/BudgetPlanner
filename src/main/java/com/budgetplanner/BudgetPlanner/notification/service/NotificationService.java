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
            List<Budget> budgets = expenseAdvisorService.getBudgetsForUser(user.getId());

            int remainingDays = expenseAdvisorService.calculateRemainingDays();
            long budget = expenseAdvisorService.getBudgets(budgets);
            long spentAmount = expenseAdvisorService.amountUsedThisMonth(user);
            int dailyAmount = expenseAdvisorService.calculateDailyAmount(remainingDays, budget, spentAmount);

            Map<Category, Double> categoryRatios = expenseAdvisorService.calculateCategoryRatios(budgets, budget);
            Map<Category, Integer> categoryBudgets = expenseAdvisorService.getCategoryBudgets(budgets, dailyAmount, categoryRatios);

            String comment = (dailyAmount < DAILY_MIN_BUDGET) ?
                    "이번 달 소비가 많습니다. 오늘은 절약하시는 것을 추천드립니다! 화이팅!" : "이번 달 소비 계획이 잘 지켜지고 있습니다!";

            sendUserBudgetRecommendMessage(user, dailyAmount, categoryBudgets, comment);
        }

    }

    public void sendGuideMessages() throws JsonProcessingException {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Expense> expenses = expenseAdvisorService.getExpensesToday(user);
            int todaySpentAmount = expenseAdvisorService.calculateTodaySpentAmount(expenses);
            Map<Category, Integer> todayCategorySpent = expenseAdvisorService.calculateTodayCategorySpent(expenses);

            List<Budget> budgets = expenseAdvisorService.getBudgetsForUser(user.getId());
            int remainingDays = expenseAdvisorService.calculateRemainingDays();
            long budget = expenseAdvisorService.getBudgets(budgets);
            long spentAmount = expenseAdvisorService.amountUsedThisMonth(user);
            int dailyAmount = expenseAdvisorService.calculateDailyAmount(remainingDays, budget, spentAmount);

            Map<Category, Double> categoryRatios = expenseAdvisorService.calculateCategoryRatios(budgets, budget);
            Map<Category, Integer> categoryBudgets = expenseAdvisorService.getCategoryBudgets(budgets, dailyAmount, categoryRatios);
            Map<Category, String> riskByCategory = expenseAdvisorService.riskByCategory(todayCategorySpent, categoryBudgets);

            sendUserBudgetGuideMessage(user, todaySpentAmount, todayCategorySpent, categoryBudgets, riskByCategory);
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
