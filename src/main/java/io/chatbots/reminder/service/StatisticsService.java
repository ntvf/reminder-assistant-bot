package io.chatbots.reminder.service;

import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.domain.ChatUserRepository;
import io.chatbots.reminder.domain.ReminderRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private static final DateTimeFormatter BUILD_TIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);

    private final ReminderRepository reminderRepository;
    private final ChatUserRepository chatUserRepository;
    private final BuildProperties buildProperties;

    public StatisticsService(ReminderRepository reminderRepository, ChatUserRepository chatUserRepository,
                             ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.reminderRepository = reminderRepository;
        this.chatUserRepository = chatUserRepository;
        this.buildProperties = buildPropertiesProvider.getIfAvailable();
    }

    public String buildStatsReport() {
        var totalUsers = chatUserRepository.count();
        var telegramUsers = chatUserRepository.countByMessengerType(MessengerType.TELEGRAM);
        var activeReminders = reminderRepository.countByActiveTrue();
        var activeUsers = reminderRepository.countDistinctActiveUsers();

        var sb = new StringBuilder("📊 Bot Statistics\n\n");
        sb.append("🏷 Version: ").append(describeBuild()).append("\n");
        sb.append("⏱ Uptime: ").append(describeUptime()).append("\n\n");
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

    private String describeBuild() {
        if (buildProperties == null) return "unknown";
        var tag = buildProperties.get("tag");
        var commit = buildProperties.get("commit");
        var sb = new StringBuilder(tag != null ? tag : buildProperties.getVersion());
        if (commit != null && !"local".equals(commit)) {
            sb.append(" (").append(commit.length() > 7 ? commit.substring(0, 7) : commit).append(")");
        }
        var time = buildProperties.getTime();
        if (time != null) {
            sb.append(" · built ").append(BUILD_TIME_FORMAT.format(time)).append(" UTC");
        }
        return sb.toString();
    }

    private static String describeUptime() {
        var uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        var days = uptime.toDays();
        if (days > 0) return days + "d " + uptime.toHoursPart() + "h";
        var hours = uptime.toHours();
        if (hours > 0) return hours + "h " + uptime.toMinutesPart() + "m";
        return uptime.toMinutes() + "m";
    }
}
