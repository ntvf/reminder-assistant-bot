package io.chatbots.reminder.scheduler;

import io.chatbots.reminder.domain.Reminder;
import io.chatbots.reminder.domain.ReminderRepository;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

@Service
public class QuartzReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(QuartzReminderScheduler.class);
    private static final String JOB_GROUP = "reminders";

    private final Scheduler scheduler;
    private final ReminderRepository reminderRepository;

    public QuartzReminderScheduler(Scheduler scheduler, ReminderRepository reminderRepository) {
        this.scheduler = scheduler;
        this.reminderRepository = reminderRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleActiveReminders() {
        log.info("Rescheduling active reminders on startup...");
        var activeReminders = reminderRepository.findByActiveTrue();
        var now = LocalDateTime.now();
        int scheduled = 0;
        int missed = 0;

        for (Reminder reminder : activeReminders) {
            try {
                if (!reminder.isRecurring() && reminder.getFireAt() != null && reminder.getFireAt().isBefore(now)) {
                    reminder.setActive(false);
                    reminderRepository.save(reminder);
                    missed++;
                    log.info("Marked past one-time reminder {} as missed (was due {})", reminder.getId(), reminder.getFireAt());
                } else {
                    schedule(reminder);
                    scheduled++;
                }
            } catch (Exception e) {
                log.error("Failed to reschedule reminder {}: {}", reminder.getId(), e.getMessage());
            }
        }
        log.info("Startup reschedule complete: {} scheduled, {} marked missed", scheduled, missed);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReminderCreated(ReminderCreatedEvent event) {
        try {
            schedule(event.reminder());
        } catch (Exception e) {
            log.error("Failed to schedule reminder {} after commit: {}", event.reminder().getId(), e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReminderDeleted(ReminderDeletedEvent event) {
        try {
            cancelReminder(event.reminder());
        } catch (Exception e) {
            log.error("Failed to cancel Quartz job for reminder {}: {}", event.reminder().getId(), e.getMessage());
        }
    }

    public void schedule(Reminder reminder) throws SchedulerException {
        var jobKey = JobKey.jobKey("reminder-" + reminder.getId(), JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        var jobData = new JobDataMap();
        jobData.put(ReminderNotificationJob.REMINDER_ID_KEY, reminder.getId());
        jobData.put(ReminderNotificationJob.CHAT_ID_KEY, reminder.getChatUser().getChatId());
        jobData.put(ReminderNotificationJob.MESSENGER_TYPE_KEY, reminder.getChatUser().getMessengerType().name());

        var jobDetail = JobBuilder.newJob(ReminderNotificationJob.class)
            .withIdentity(jobKey)
            .usingJobData(jobData)
            .storeDurably()
            .build();

        Trigger trigger;
        if (reminder.isRecurring()) {
            var tz = TimeZone.getTimeZone(reminder.getChatUser().getTimezone());
            trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger-" + reminder.getId(), JOB_GROUP)
                .withSchedule(CronScheduleBuilder
                    .cronSchedule(reminder.getCronExpression())
                    .inTimeZone(tz)
                    .withMisfireHandlingInstructionDoNothing())
                .build();
        } else {
            var zoneId = parseZoneId(reminder.getChatUser().getTimezone());
            var fireDate = Date.from(reminder.getFireAt().atZone(zoneId).toInstant());
            trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger-" + reminder.getId(), JOB_GROUP)
                .startAt(fireDate)
                .build();
        }

        scheduler.scheduleJob(jobDetail, trigger);
        reminder.setQuartzJobKey(jobKey.toString());
        reminderRepository.save(reminder);
        log.info("Scheduled reminder {} ({})", reminder.getId(), reminder.getScheduleDescription());
    }

    public void cancelReminder(Reminder reminder) throws SchedulerException {
        var jobKey = JobKey.jobKey("reminder-" + reminder.getId(), JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("Cancelled Quartz job for reminder {}", reminder.getId());
        }
    }

    private ZoneId parseZoneId(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }
}
