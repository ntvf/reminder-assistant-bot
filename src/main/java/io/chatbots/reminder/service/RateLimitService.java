package io.chatbots.reminder.service;

import io.chatbots.reminder.config.AppProperties;
import io.chatbots.reminder.domain.ChatDailyLimitRepository;
import io.chatbots.reminder.bot.MessengerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RateLimitService {

    private final ChatDailyLimitRepository repo;
    private final AppProperties appProperties;

    public RateLimitService(ChatDailyLimitRepository repo, AppProperties appProperties) {
        this.repo = repo;
        this.appProperties = appProperties;
    }

    @Transactional
    public void checkAndIncrement(String chatId, MessengerType messengerType, String languageCode) {
        var today = LocalDate.now();
        repo.upsertIncrement(chatId, messengerType.name(), today);
        var entry = repo.findByKey(chatId, messengerType, today).orElseThrow();
        if (entry.getRequestCount() > appProperties.dailyReminderLimit()) {
            throw new RateLimitExceededException(
                BotMessages.get(BotMessages.Key.RATE_LIMITED, languageCode, appProperties.dailyReminderLimit())
            );
        }
    }
}
