package io.chatbots.reminder.service;

import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.domain.ChatUserRepository;
import io.chatbots.reminder.domain.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock ReminderRepository reminderRepository;
    @Mock ChatUserRepository chatUserRepository;
    @Mock ObjectProvider<BuildProperties> buildPropertiesProvider;

    StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsService(reminderRepository, chatUserRepository, buildPropertiesProvider);
    }

    private static BuildProperties buildProperties(String tag, String commit) {
        var props = new Properties();
        props.setProperty("time", String.valueOf(Instant.parse("2026-07-28T11:20:00Z").toEpochMilli()));
        props.setProperty("version", "0.0.1-SNAPSHOT");
        props.setProperty("tag", tag);
        props.setProperty("commit", commit);
        return new BuildProperties(props);
    }

    @Test
    void buildStatsReport_returnsFormattedReport() {
        when(chatUserRepository.count()).thenReturn(50L);
        when(chatUserRepository.countByMessengerType(MessengerType.TELEGRAM)).thenReturn(50L);
        when(reminderRepository.countByActiveTrue()).thenReturn(120L);
        when(reminderRepository.countDistinctActiveUsers()).thenReturn(30L);
        when(reminderRepository.countActiveByLanguage()).thenReturn(List.of(
            new Object[]{"en", 80L},
            new Object[]{"de", 20L},
            new Object[]{null, 20L}
        ));
        when(chatUserRepository.countBySource()).thenReturn(List.of());

        var report = statisticsService.buildStatsReport();

        assertThat(report).contains("50").contains("120").contains("30").contains("en").contains("de").contains("unknown");
    }

    @Test
    void buildStatsReport_withBuildInfo_showsVersionAndCommit() {
        when(buildPropertiesProvider.getIfAvailable()).thenReturn(buildProperties("v1.0.42", "a1b2c3d4e5f6"));
        statisticsService = new StatisticsService(reminderRepository, chatUserRepository, buildPropertiesProvider);
        when(chatUserRepository.count()).thenReturn(0L);
        when(chatUserRepository.countByMessengerType(MessengerType.TELEGRAM)).thenReturn(0L);
        when(reminderRepository.countByActiveTrue()).thenReturn(0L);
        when(reminderRepository.countDistinctActiveUsers()).thenReturn(0L);
        when(reminderRepository.countActiveByLanguage()).thenReturn(List.of());
        when(chatUserRepository.countBySource()).thenReturn(List.of());

        var report = statisticsService.buildStatsReport();

        assertThat(report).contains("Version: v1.0.42 (a1b2c3d) · built 2026-07-28 11:20 UTC");
        assertThat(report).contains("Uptime:");
    }

    @Test
    void buildStatsReport_withoutBuildInfo_showsUnknownVersion() {
        when(chatUserRepository.count()).thenReturn(0L);
        when(chatUserRepository.countByMessengerType(MessengerType.TELEGRAM)).thenReturn(0L);
        when(reminderRepository.countByActiveTrue()).thenReturn(0L);
        when(reminderRepository.countDistinctActiveUsers()).thenReturn(0L);
        when(reminderRepository.countActiveByLanguage()).thenReturn(List.of());
        when(chatUserRepository.countBySource()).thenReturn(List.of());

        var report = statisticsService.buildStatsReport();

        assertThat(report).contains("Version: unknown");
    }

    @Test
    void buildStatsReport_withSources_showsActivationRate() {
        when(chatUserRepository.count()).thenReturn(40L);
        when(chatUserRepository.countByMessengerType(MessengerType.TELEGRAM)).thenReturn(40L);
        when(reminderRepository.countByActiveTrue()).thenReturn(15L);
        when(reminderRepository.countDistinctActiveUsers()).thenReturn(12L);
        when(reminderRepository.countActiveByLanguage()).thenReturn(List.of());
        when(chatUserRepository.countBySource()).thenReturn(List.of(
            new Object[]{"ads_ua_1", 30L},
            new Object[]{"ads_en_1", 10L}
        ));
        when(reminderRepository.countActivatedUsersBySource()).thenReturn(List.<Object[]>of(
            new Object[]{"ads_ua_1", 12L}
        ));

        var report = statisticsService.buildStatsReport();

        assertThat(report).contains("ads_ua_1: 12 / 30 (40%)");
        assertThat(report).contains("ads_en_1: 0 / 10 (0%)");
    }

    @Test
    void buildStatsReport_noLanguageData_displaysCorrectly() {
        when(chatUserRepository.count()).thenReturn(0L);
        when(chatUserRepository.countByMessengerType(MessengerType.TELEGRAM)).thenReturn(0L);
        when(reminderRepository.countByActiveTrue()).thenReturn(0L);
        when(reminderRepository.countDistinctActiveUsers()).thenReturn(0L);
        when(reminderRepository.countActiveByLanguage()).thenReturn(List.of());
        when(chatUserRepository.countBySource()).thenReturn(List.of());

        var report = statisticsService.buildStatsReport();
        assertThat(report).contains("Statistics");
    }
}
