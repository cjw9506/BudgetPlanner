package com.budgetplanner.BudgetPlanner.common.scheduler;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.Calendar;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetailFactoryBean jobDetail1() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(ScheduledJob.class);
        jobDetailFactory.setGroup("myJobGroup1");
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean trigger1(@Qualifier("jobDetail1") JobDetail jobDetail) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 32);
        cal.set(Calendar.SECOND, 00);

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setStartTime(cal.getTime());
        trigger.setRepeatInterval(24 * 60 * 60 * 1000);
        return trigger;
    }

    @Bean
    public JobDetailFactoryBean jobDetail2() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(ScheduledJob.class);
        jobDetailFactory.setGroup("myJobGroup2");
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean trigger2(@Qualifier("jobDetail2") JobDetail jobDetail) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 32);
        cal.set(Calendar.SECOND, 00);

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setStartTime(cal.getTime());
        trigger.setRepeatInterval(24 * 60 * 60 * 1000);
        return trigger;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger1, Trigger trigger2) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();

        schedulerFactory.setTriggers(trigger1, trigger2);
        return schedulerFactory;
    }

}
