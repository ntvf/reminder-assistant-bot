package io.chatbots.reminder.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ReminderAiService {

    // JSON Schema for OpenAI Structured Outputs (strict: true is applied automatically by Spring AI)
    private static final String RESPONSE_SCHEMA = """
        {
          "type": "object",
          "properties": {
            "reminderText":        {"type": "string"},
            "recurring":           {"type": "boolean"},
            "cronExpression":      {"anyOf": [{"type": "string"}, {"type": "null"}]},
            "fireAt":              {"anyOf": [{"type": "string"}, {"type": "null"}]},
            "scheduleDescription": {"anyOf": [{"type": "string"}, {"type": "null"}]},
            "valid":               {"type": "boolean"},
            "errorMessage":        {"anyOf": [{"type": "string"}, {"type": "null"}]},
            "chain": {
              "anyOf": [
                {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "reminderText":        {"type": "string"},
                      "cronExpression":      {"anyOf": [{"type": "string"}, {"type": "null"}]},
                      "fireAt":              {"anyOf": [{"type": "string"}, {"type": "null"}]},
                      "scheduleDescription": {"anyOf": [{"type": "string"}, {"type": "null"}]}
                    },
                    "required": ["reminderText", "cronExpression", "fireAt", "scheduleDescription"],
                    "additionalProperties": false
                  }
                },
                {"type": "null"}
              ]
            },
            "detectedLanguageCode": {"anyOf": [{"type": "string"}, {"type": "null"}]}
          },
          "required": ["reminderText", "recurring", "cronExpression", "fireAt",
                       "scheduleDescription", "valid", "errorMessage", "chain",
                       "detectedLanguageCode"],
          "additionalProperties": false
        }
        """;

    private static final OpenAiChatModel.ResponseFormat STRUCTURED_FORMAT =
        OpenAiChatModel.ResponseFormat.builder()
            .type(OpenAiChatModel.ResponseFormat.Type.JSON_SCHEMA)
            .jsonSchema(RESPONSE_SCHEMA)
            .build();

    private final ChatClient chatClient;
    private final PromptSanitizerService promptSanitizerService;

    public ReminderAiService(ChatClient.Builder chatClientBuilder, PromptSanitizerService promptSanitizerService) {
        this.chatClient = chatClientBuilder.build();
        this.promptSanitizerService = promptSanitizerService;
    }

    public ReminderParseResult parseReminder(String userMessage, String timezone, String languageCode) {
        var zoneId = parseZoneId(timezone);
        var now = LocalDateTime.now(zoneId);
        var systemPrompt = promptSanitizerService.buildSystemPrompt(languageCode);

        // Temporal context goes in user message — keeps system prompt static for prompt caching
        var temporalContext = "[Temporal context: " + now + " | Timezone: " + timezone + "]\n";

        return chatClient.prompt()
            .system(systemPrompt)
            .user(temporalContext + userMessage)
            .options(OpenAiChatOptions.builder().responseFormat(STRUCTURED_FORMAT))
            .call()
            .entity(ReminderParseResult.class);
    }

    private ZoneId parseZoneId(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }
}
