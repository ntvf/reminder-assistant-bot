package io.chatbots.reminder.service;

import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.domain.ChatUserRepository;
import io.chatbots.reminder.domain.ReminderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock ReminderRepository reminderRepository;
    @Mock ChatUserRepository chatUserRepository;

    @InjectMocks
    StatisticsService statisticsService;

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

        var report = statisticsService.buildStatsReport();

        assertThat(report).contains("50").contains("120").contains("30").contains("en").contains("de").contains("unknown");
    }

    @Test
    void buildStatsReport_noLanguageData_displaysCorrectly() {
        when(chatUserRepository.count()).thenReturn(0L);
        when(chatUserRepository.countByMessengerType(MessengerType.TELEGRAM)).thenReturn(0L);
        when(reminderRepository.countByActiveTrue()).thenReturn(0L);
        when(reminderRepository.countDistinctActiveUsers()).thenReturn(0L);
        when(reminderRepository.countActiveByLanguage()).thenReturn(List.of());

        var report = statisticsService.buildStatsReport();
        assertThat(report).contains("Statistics");
    }
}
