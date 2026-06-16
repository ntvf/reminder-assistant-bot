package io.chatbots.reminder.integration;

import io.chatbots.reminder.ai.ReminderAiService;
import io.chatbots.reminder.ai.ReminderParseResult;
import io.chatbots.reminder.bot.MessengerMessage;
import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.bot.telegram.TelegramBotService;
import io.chatbots.reminder.domain.ChatUserRepository;
import io.chatbots.reminder.domain.ReminderRepository;
import io.chatbots.reminder.service.MaxRemindersExceededException;
import io.chatbots.reminder.service.ReminderService;
import io.chatbots.reminder.service.StatisticsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ReminderIntegrationTest {

    @Autowired ReminderService reminderService;
    @Autowired ReminderRepository reminderRepository;
    @Autowired ChatUserRepository chatUserRepository;
    @Autowired StatisticsService statisticsService;

    @MockitoBean ReminderAiService reminderAiService;
    @MockitoBean TelegramBotService telegramBotService;

    @AfterEach
    void cleanup() {
        reminderRepository.deleteAll();
        chatUserRepository.deleteAll();
    }

    @Test
    void createReminder_persistedToDatabase() {
        var parseResult = new ReminderParseResult("Feed the leaven", null, true, "0 0 18 ? * FRI",
            null, "Every Friday at 18:00", true, null, null, null, false);
        when(reminderAiService.parseReminder(anyString(), anyString(), any())).thenReturn(parseResult);

        var msg = new MessengerMessage("chat-1", MessengerType.TELEGRAM, "Remind me every friday to feed the leaven", "testuser", 1L);
        var result = reminderService.createReminder(msg, "en");

        assertThat(result).contains("✅");
        var user = chatUserRepository.findByChatIdAndMessengerType("chat-1", MessengerType.TELEGRAM);
        assertThat(user).isPresent();
        var reminders = reminderRepository.findByChatUserAndActiveTrueOrderByIdAsc(user.get());
        assertThat(reminders).hasSize(1);
        assertThat(reminders.get(0).getReminderText()).isEqualTo("Feed the leaven");
        assertThat(reminders.get(0).isRecurring()).isTrue();
    }

    @Test
    void listReminders_afterCreate_showsReminder() {
        var parseResult = new ReminderParseResult("Buy flowers", null, false, null,
            LocalDateTime.now().plusDays(7), "In one week", true, null, null, null, false);
        when(reminderAiService.parseReminder(anyString(), anyString(), any())).thenReturn(parseResult);

        var msg = new MessengerMessage("chat-2", MessengerType.TELEGRAM, "Remind me in one week to buy flowers", "user2", 2L);
        reminderService.createReminder(msg, "en");

        var list = reminderService.listReminders("chat-2", MessengerType.TELEGRAM);
        assertThat(list).contains("Buy flowers").contains("In one week");
    }

    @Test
    void deleteReminder_removesFromActiveList() {
        var parseResult = new ReminderParseResult("Call dentist", null, false, null,
            LocalDateTime.now().plusDays(1), "Tomorrow", true, null, null, null, false);
        when(reminderAiService.parseReminder(anyString(), anyString(), any())).thenReturn(parseResult);

        var msg = new MessengerMessage("chat-3", MessengerType.TELEGRAM, "Remind me tomorrow to call dentist", "user3", 3L);
        reminderService.createReminder(msg, "en");

        var user = chatUserRepository.findByChatIdAndMessengerType("chat-3", MessengerType.TELEGRAM);
        var reminders = reminderRepository.findByChatUserAndActiveTrueOrderByIdAsc(user.orElseThrow());
        assertThat(reminders).hasSize(1);

        var reminderId = reminders.getFirst().getId();
        var deleteResult = reminderService.deleteReminder("chat-3", MessengerType.TELEGRAM, reminderId);
        assertThat(deleteResult).contains("✅");

        var afterDelete = reminderRepository.findByChatUserAndActiveTrueOrderByIdAsc(user.orElseThrow());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void maxRemindersLimit_enforced() {
        var parseResult = new ReminderParseResult("Reminder", null, true, "0 0 9 * * ?",
            null, "Every day at 9", true, null, null, null, false);
        when(reminderAiService.parseReminder(anyString(), anyString(), any())).thenReturn(parseResult);

        var chatId = "chat-4";
        for (int i = 0; i < 10; i++) {
            var msg = new MessengerMessage(chatId, MessengerType.TELEGRAM, "Remind me daily", "user4", 4L);
            reminderService.createReminder(msg, "en");
        }

        var msg = new MessengerMessage(chatId, MessengerType.TELEGRAM, "Remind me daily again", "user4", 4L);
        assertThatThrownBy(() -> reminderService.createReminder(msg, "en"))
            .isInstanceOf(MaxRemindersExceededException.class);
    }

    @Test
    void statistics_returnsCorrectCounts() {
        var parseResult = new ReminderParseResult("Test", null, true, "0 0 9 * * ?",
            null, "Every day", true, null, null, null, false);
        when(reminderAiService.parseReminder(anyString(), anyString(), any())).thenReturn(parseResult);

        reminderService.createReminder(new MessengerMessage("chat-5", MessengerType.TELEGRAM, "Remind me", "u5", 5L), "en");
        reminderService.createReminder(new MessengerMessage("chat-6", MessengerType.TELEGRAM, "Remind me", "u6", 6L), "de");

        var stats = statisticsService.buildStatsReport();
        assertThat(stats).contains("2");
    }

    @Test
    void updateTimezone_persistsToDatabase() {
        var parseResult = new ReminderParseResult("Test", null, true, "0 0 9 * * ?",
            null, "Every day", true, null, null, null, false);
        when(reminderAiService.parseReminder(anyString(), anyString(), any())).thenReturn(parseResult);
        reminderService.createReminder(new MessengerMessage("chat-7", MessengerType.TELEGRAM, "Remind me daily", "u7", 7L), "en");

        var result = reminderService.updateTimezone("chat-7", MessengerType.TELEGRAM, "Europe/Prague");
        assertThat(result).contains("✅");

        var user = chatUserRepository.findByChatIdAndMessengerType("chat-7", MessengerType.TELEGRAM);
        assertThat(user).isPresent();
        assertThat(user.orElseThrow().getTimezone()).isEqualTo("Europe/Prague");
    }

    @Test
    void createReminder_withChain_persistsAllReminders() {
        var chain = List.of(
            new io.chatbots.reminder.ai.ChainedReminder(
                "🎁 One week until wife's birthday — buy a gift!", null,
                "0 0 9 8 3 ?", null, "One week before, yearly"),
            new io.chatbots.reminder.ai.ChainedReminder(
                "🎂 Tomorrow is wife's birthday!", null,
                "0 0 9 14 3 ?", null, "Day before, yearly")
        );
        var parseResult = new ReminderParseResult(
            "🎉 Happy birthday to your wife!", null, true, "0 0 9 15 3 ?",
            null, "Every March 15 at 09:00", true, null, chain, null, false);
        when(reminderAiService.parseReminder(anyString(), anyString(), any())).thenReturn(parseResult);

        var msg = new MessengerMessage("chat-8", MessengerType.TELEGRAM,
            "Remind me about my wife's birthday on March 15", "u8", 8L);
        var result = reminderService.createReminder(msg, "en");

        assertThat(result).contains("✅").contains("lead-up reminders");

        var user = chatUserRepository.findByChatIdAndMessengerType("chat-8", MessengerType.TELEGRAM);
        var reminders = reminderRepository.findByChatUserAndActiveTrueOrderByIdAsc(user.orElseThrow());
        assertThat(reminders).hasSize(3);
    }
}
