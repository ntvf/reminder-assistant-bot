package io.chatbots.reminder.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;
import java.util.List;

public record ReminderParseResult(
    @JsonProperty("reminderText") String reminderText,
    @JsonProperty("recurring") boolean recurring,
    @JsonProperty("cronExpression") String cronExpression,
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    @JsonProperty("fireAt") LocalDateTime fireAt,
    @JsonProperty("scheduleDescription") String scheduleDescription,
    @JsonProperty("valid") boolean valid,
    @JsonProperty("errorMessage") String errorMessage,
    @JsonProperty("chain") List<ChainedReminder> chain,
    @JsonProperty("detectedLanguageCode") String detectedLanguageCode,
    @JsonProperty("preEventChoice") boolean preEventChoice
) {}
