package io.chatbots.reminder.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;

public record ChainedReminder(
    @JsonProperty("reminderText") String reminderText,
    @JsonProperty("cronExpression") String cronExpression,
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    @JsonProperty("fireAt") LocalDateTime fireAt,
    @JsonProperty("scheduleDescription") String scheduleDescription
) {}
