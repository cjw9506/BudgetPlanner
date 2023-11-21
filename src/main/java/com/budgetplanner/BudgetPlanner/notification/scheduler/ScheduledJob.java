package com.budgetplanner.BudgetPlanner.notification.scheduler;

import com.budgetplanner.BudgetPlanner.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ScheduledJob implements Job {

    private final NotificationService notificationService;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Trigger trigger = context.getTrigger();
        String triggerName = trigger.getKey().getName();

        if ("recommendMessagesTrigger".equals(triggerName)) {
            log.info("오늘 지출 추천 메시지 발송 시작 - {}", ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
            notificationService.sendRecommendMessages();
            log.info("오늘 지출 추천 메시지 발송 완료 - {}", ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        } else if ("guideMessagesTrigger".equals(triggerName)) {
            log.info("오늘 지출 안내 메시지 발송 시작 - {}", ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
            notificationService.sendGuideMessages();
            log.info("오늘 지출 안내 메시지 발송 완료 - {}", ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        }



    }
}
