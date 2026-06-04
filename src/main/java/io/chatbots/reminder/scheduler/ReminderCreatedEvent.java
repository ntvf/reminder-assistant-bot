package io.chatbots.reminder.scheduler;

import io.chatbots.reminder.domain.Reminder;

public record ReminderCreatedEvent(Reminder reminder) {
}
