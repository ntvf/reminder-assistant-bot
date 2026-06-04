package io.chatbots.reminder.scheduler;

import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.domain.ChatUser;
import io.chatbots.reminder.domain.Reminder;
import io.chatbots.reminder.domain.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuartzReminderSchedulerTest {

    @Mock Scheduler scheduler;
    @Mock ReminderRepository reminderRepository;

    private QuartzReminderScheduler quartzScheduler;

    @BeforeEach
    void setUp() {
        quartzScheduler = new QuartzReminderScheduler(scheduler, reminderRepository);
    }

    @Test
    void schedule_recurringReminder_createsCronTrigger() throws Exception {
        var reminder = createRecurringReminder(1L, "0 0 18 ? * FRI");
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        quartzScheduler.schedule(reminder);

        var triggerCaptor = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler).scheduleJob(any(JobDetail.class), triggerCaptor.capture());
        assertThat(triggerCaptor.getValue()).isInstanceOf(CronTrigger.class);
        assertThat(((CronTrigger) triggerCaptor.getValue()).getCronExpression()).isEqualTo("0 0 18 ? * FRI");
    }

    @Test
    void schedule_oneTimeReminder_createsSimpleTrigger() throws Exception {
        var reminder = createOneTimeReminder(2L, LocalDateTime.now().plusDays(7));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        quartzScheduler.schedule(reminder);

        var triggerCaptor = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler).scheduleJob(any(JobDetail.class), triggerCaptor.capture());
        assertThat(triggerCaptor.getValue()).isInstanceOf(SimpleTrigger.class);
    }

    @Test
    void schedule_existingJob_deletesAndReschedules() throws Exception {
        var reminder = createRecurringReminder(3L, "0 0 9 * * ?");
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        quartzScheduler.schedule(reminder);

        verify(scheduler).deleteJob(any(JobKey.class));
        verify(scheduler).scheduleJob(any(), any());
    }

    @Test
    void cancelReminder_existingJob_deletes() throws Exception {
        var reminder = createRecurringReminder(4L, "0 0 9 * * ?");
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        quartzScheduler.cancelReminder(reminder);

        verify(scheduler).deleteJob(any(JobKey.class));
    }

    @Test
    void cancelReminder_nonExistingJob_doesNothing() throws Exception {
        var reminder = createRecurringReminder(5L, "0 0 9 * * ?");
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        quartzScheduler.cancelReminder(reminder);

        verify(scheduler, never()).deleteJob(any(JobKey.class));
    }

    @Test
    void rescheduleActiveReminders_pastOneTime_markedMissed() throws Exception {
        var pastReminder = createOneTimeReminder(6L, LocalDateTime.now().minusDays(1));
        when(reminderRepository.findByActiveTrue()).thenReturn(List.of(pastReminder));

        quartzScheduler.rescheduleActiveReminders();

        assertThat(pastReminder.isActive()).isFalse();
        verify(reminderRepository).save(pastReminder);
        verify(scheduler, never()).scheduleJob(any(), any());
    }

    @Test
    void rescheduleActiveReminders_futureOneTime_schedulesIt() throws Exception {
        var futureReminder = createOneTimeReminder(7L, LocalDateTime.now().plusDays(1));
        when(reminderRepository.findByActiveTrue()).thenReturn(List.of(futureReminder));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        quartzScheduler.rescheduleActiveReminders();

        assertThat(futureReminder.isActive()).isTrue();
        verify(scheduler).scheduleJob(any(), any());
    }

    private Reminder createRecurringReminder(Long id, String cron) {
        var chatUser = new ChatUser("chat-" + id, MessengerType.TELEGRAM, "en");
        var reminder = new Reminder();
        setId(reminder, id);
        reminder.setChatUser(chatUser);
        reminder.setReminderText("Test reminder");
        reminder.setRecurring(true);
        reminder.setCronExpression(cron);
        reminder.setActive(true);
        return reminder;
    }

    private Reminder createOneTimeReminder(Long id, LocalDateTime fireAt) {
        var chatUser = new ChatUser("chat-" + id, MessengerType.TELEGRAM, "en");
        var reminder = new Reminder();
        setId(reminder, id);
        reminder.setChatUser(chatUser);
        reminder.setReminderText("Test reminder");
        reminder.setRecurring(false);
        reminder.setFireAt(fireAt);
        reminder.setActive(true);
        return reminder;
    }

    private void setId(Reminder reminder, Long id) {
        try {
            var field = Reminder.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(reminder, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
