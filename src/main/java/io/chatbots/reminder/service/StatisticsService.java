package io.chatbots.reminder.service;

import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.domain.ChatUserRepository;
import io.chatbots.reminder.domain.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final ReminderRepository reminderRepository;
    private final ChatUserRepository chatUserRepository;

    public StatisticsService(ReminderRepository reminderRepository, ChatUserRepository chatUserRepository) {
        this.reminderRepository = reminderRepository;
        this.chatUserRepository = chatUserRepository;
    }

    public String buildStatsReport() {
        var totalUsers = chatUserRepository.count();
        var telegramUsers = chatUserRepository.countByMessengerType(MessengerType.TELEGRAM);
        var activeReminders = reminderRepository.countByActiveTrue();
        var activeUsers = reminderRepository.countDistinctActiveUsers();

        var sb = new StringBuilder("📊 Bot Statistics\n\n");
        sb.append("�� Total users: ").append(totalUsers).append("\n");
        sb.append("📱 Telegram users: ").append(telegramUsers).append("\n");
        sb.append("👤 Active users (with reminders): ").append(activeUsers).append("\n");
        sb.append("🔔 Active reminders: ").append(activeReminders).append("\n\n");
        sb.append("🌐 Language breakdown:\n");

        var langCounts = reminderRepository.countActiveByLanguage();
        for (var row : langCounts) {
            var lang = row[0] != null ? (String) row[0] : "unknown";
            var count = (Long) row[1];
            sb.append("  • ").append(lang).append(": ").append(count).append("\n");
        }

        var sourceCounts = chatUserRepository.countBySource();
        if (!sourceCounts.isEmpty()) {
            var activated = reminderRepository.countActivatedUsersBySource().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));
            sb.append("\n📣 Acquisition source (activated / joined):\n");
            for (var row : sourceCounts) {
                var source = (String) row[0];
                var joined = (Long) row[1];
                var active = activated.getOrDefault(source, 0L);
                sb.append("  • ").append(source).append(": ").append(active).append(" / ").append(joined)
                    .append(" (").append(Math.round(100.0 * active / joined)).append("%)\n");
            }
        }
        return sb.toString().trim();
    }
}
