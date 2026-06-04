package io.chatbots.reminder;

import io.chatbots.reminder.ai.ReminderAiService;
import io.chatbots.reminder.bot.telegram.TelegramBotService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class ReminderApplicationTests {

    @MockitoBean
    TelegramBotService telegramBotService;

    @MockitoBean
    ReminderAiService reminderAiService;

    @Test
    void contextLoads() {
    }
}
