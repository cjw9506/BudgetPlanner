package com.budgetplanner.BudgetPlanner.notification.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.TimeZone;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    final String EVERY_MORNING = "00 00 10 * * ?";
    final String EVERY_AFTERNOON = "00 00 22 * * ?";

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(ScheduledJob.class)
                .storeDurably()
                .withIdentity("discordNotificationJobDetail")
                .withDescription("Invoke Discord Notification service...")
                .build();
    }

    @Bean
    public Trigger recommendMessagesTrigger(
            @Qualifier("jobDetail") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("recommendMessagesTrigger")
                .withSchedule(cronSchedule(EVERY_MORNING)
                        .inTimeZone(TimeZone.getTimeZone("Asia/Seoul")))
                .build();
    }

    @Bean
    public Trigger guideMessagesTrigger(
            @Qualifier("jobDetail") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("guideMessagesTrigger")
                .withSchedule(cronSchedule(EVERY_AFTERNOON)
                        .inTimeZone(TimeZone.getTimeZone("Asia/Seoul")))
                .build();
    }

    @Bean
    public Scheduler discordJobScheduler(
            @Qualifier("jobDetail") JobDetail jobDetail,
            @Qualifier("recommendMessagesTrigger") Trigger recommendTrigger,
            @Qualifier("guideMessagesTrigger") Trigger guideTrigger,
            SchedulerFactoryBean schedulerFactoryBean
    ) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (!scheduler.checkExists(jobDetail.getKey())) {
            scheduler.scheduleJob(jobDetail, recommendTrigger);
            scheduler.scheduleJob(jobDetail, guideTrigger);
        } else {
            scheduler.addJob(jobDetail, true, true);
            if (!scheduler.checkExists(recommendTrigger.getKey())) {
                scheduler.scheduleJob(recommendTrigger);
            }
            if (!scheduler.checkExists(guideTrigger.getKey())) {
                scheduler.scheduleJob(guideTrigger);
            }
        }
        return scheduler;
    }

}
