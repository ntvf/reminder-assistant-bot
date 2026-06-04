package io.chatbots.reminder.scheduler;

import io.chatbots.reminder.domain.Reminder;

public record ReminderDeletedEvent(Reminder reminder) {
}
