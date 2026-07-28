package io.chatbots.reminder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.domain.AiInteraction;
import io.chatbots.reminder.domain.AiInteractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AiInteractionLogService {

    private static final Logger log = LoggerFactory.getLogger(AiInteractionLogService.class);

    private static final int MAX_TEXT_LENGTH = 8000;

    private final AiInteractionRepository aiInteractionRepository;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public AiInteractionLogService(AiInteractionRepository aiInteractionRepository) {
        this.aiInteractionRepository = aiInteractionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String chatId, MessengerType messengerType, String requestText, String sanitizedText,
                       String languageCode, String timezone, Object response, String outcome,
                       String errorText, long latencyMs) {
        try {
            var interaction = new AiInteraction();
            interaction.setChatId(chatId);
            interaction.setMessengerType(messengerType);
            interaction.setRequestText(truncate(requestText));
            interaction.setSanitizedText(truncate(sanitizedText));
            interaction.setLanguageCode(languageCode);
            interaction.setTimezone(timezone);
            interaction.setResponseJson(truncate(toJson(response)));
            interaction.setOutcome(outcome);
            interaction.setErrorText(truncate(errorText));
            interaction.setLatencyMs(latencyMs);
            interaction.setCreatedAt(LocalDateTime.now());
            aiInteractionRepository.save(interaction);
        } catch (Exception e) {
            log.warn("Failed to log AI interaction for chat {}: {}", chatId, e.getMessage());
        }
    }

    private String toJson(Object response) {
        if (response == null) return null;
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return String.valueOf(response);
        }
    }

    private static String truncate(String text) {
        if (text == null) return null;
        return text.length() > MAX_TEXT_LENGTH ? text.substring(0, MAX_TEXT_LENGTH) : text;
    }
}
