package io.chatbots.reminder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
    int maxRemindersPerChat,
    int dailyReminderLimit
) {
}
