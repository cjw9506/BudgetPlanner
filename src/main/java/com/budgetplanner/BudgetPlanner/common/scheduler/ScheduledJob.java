package com.budgetplanner.BudgetPlanner.common.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Calendar;
import java.util.Map;

@Component
public class ScheduledJob implements Job {


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:8080/api/budgets/categories")
                .build();

        if (hour == 0 && minute == 32 && second == 00) {
            String responseBody = client.get().retrieve().bodyToMono(String.class).block();
            String discordMessage = "```\n" + responseBody + "\n```";

            WebClient discordWebClient = WebClient.builder()
                    .baseUrl("https://discord.com/api/webhooks/1176176437955395634/M5CiObougLdznC328pf8bkbw-l0PrKk27YZb3ytcqWfx5p8WqPWRJiyQCg-dc0vTwhXU")
                    .build();

            try {
                // ObjectMapper를 사용하여 JSON 문자열 생성
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonContent = objectMapper.writeValueAsString(Map.of("content", discordMessage));

                String response = discordWebClient.post()
                        .header("Content-Type", "application/json")
                        .body(BodyInserters.fromValue(jsonContent))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                System.out.println("Discord Webhook response: " + response);
            } catch (JsonProcessingException | WebClientResponseException e) {
                System.err.println("Discord Webhook request failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
