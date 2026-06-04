package io.chatbots.reminder.ai;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Deserializes a datetime string into LocalDateTime, tolerating an optional
 * UTC offset (e.g. "+02:00") that the AI may append to the ISO-8601 value.
 * The offset is stripped — the local time component is preserved as-is.
 */
public class FlexibleLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    public FlexibleLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) {
        String text = p.getString();
        if (text == null || text.isBlank()) return null;
        try {
            return OffsetDateTime.parse(text).toLocalDateTime();
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(text);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Cannot parse LocalDateTime from: " + text, e2);
            }
        }
    }
}
