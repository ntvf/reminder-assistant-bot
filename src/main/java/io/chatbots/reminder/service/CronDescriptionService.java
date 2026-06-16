package io.chatbots.reminder.service;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.cronutils.model.CronType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

@Service
public class CronDescriptionService {

    private static final Logger log = LoggerFactory.getLogger(CronDescriptionService.class);

    private static final CronParser QUARTZ_PARSER =
        new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));

    public String resolve(String aiDescription, String cronExpression, LocalDateTime fireAt,
                          boolean recurring, String languageCode) {
        if (aiDescription != null && !aiDescription.isBlank()) {
            return aiDescription;
        }
        if (recurring && cronExpression != null) {
            return describeCron(cronExpression, languageCode);
        }
        if (fireAt != null) {
            return describeFireAt(fireAt, languageCode);
        }
        return "—";
    }

    private String describeCron(String expr, String languageCode) {
        try {
            var cron = QUARTZ_PARSER.parse(expr);
            return CronDescriptor.instance(resolveLocale(languageCode)).describe(cron);
        } catch (Exception e) {
            log.debug("cron-utils failed to describe '{}': {}", expr, e.getMessage());
            return expr;
        }
    }

    private String describeFireAt(LocalDateTime fireAt, String languageCode) {
        try {
            var locale = resolveDateLocale(languageCode);
            return fireAt.format(DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                .withLocale(locale));
        } catch (Exception e) {
            return fireAt.toString();
        }
    }

    private static final Map<String, String[]> RELATIVE_DAYS = Map.ofEntries(
        Map.entry("en", new String[]{"today", "tomorrow"}),
        Map.entry("ru", new String[]{"сегодня", "завтра"}),
        Map.entry("uk", new String[]{"сьогодні", "завтра"}),
        Map.entry("de", new String[]{"heute", "morgen"}),
        Map.entry("fr", new String[]{"aujourd'hui", "demain"}),
        Map.entry("es", new String[]{"hoy", "mañana"}),
        Map.entry("pt", new String[]{"hoje", "amanhã"}),
        Map.entry("it", new String[]{"oggi", "domani"}),
        Map.entry("tr", new String[]{"bugün", "yarın"}),
        Map.entry("pl", new String[]{"dziś", "jutro"}));

    public String relativeDayWord(LocalDateTime from, LocalDateTime event, String languageCode) {
        if (from == null || event == null) return null;
        long days = ChronoUnit.DAYS.between(from.toLocalDate(), event.toLocalDate());
        if (days != 0 && days != 1) return null;
        var lang = languageCode == null ? "en"
            : (languageCode.length() >= 2 ? languageCode.substring(0, 2).toLowerCase() : languageCode);
        return RELATIVE_DAYS.getOrDefault(lang, RELATIVE_DAYS.get("en"))[(int) days];
    }

    public LocalDateTime nextFireTime(String cronExpression, String timezone) {
        try {
            ZoneId zone;
            try { zone = ZoneId.of(timezone); } catch (Exception e) { zone = ZoneId.of("UTC"); }
            var cron = QUARTZ_PARSER.parse(cronExpression);
            return ExecutionTime.forCron(cron)
                .nextExecution(ZonedDateTime.now(zone))
                .map(ZonedDateTime::toLocalDateTime)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private Locale resolveLocale(String languageCode) {
        if (languageCode == null) return Locale.ENGLISH;
        var lang = languageCode.length() >= 2 ? languageCode.substring(0, 2).toLowerCase() : languageCode;
        return switch (lang) {
            case "de" -> Locale.GERMAN;
            case "fr" -> Locale.FRENCH;
            case "es" -> Locale.of("es");
            case "pt" -> Locale.of("pt");
            case "it" -> Locale.ITALIAN;
            case "tr" -> Locale.of("tr");
            case "pl" -> Locale.of("pl");
            case "zh" -> Locale.CHINESE;
            case "ko" -> Locale.KOREAN;
            case "ar" -> Locale.of("ar");
            case "nl" -> Locale.of("nl");
            default   -> Locale.ENGLISH;
        };
    }

    private Locale resolveDateLocale(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) return Locale.ENGLISH;
        var lang = languageCode.length() >= 2 ? languageCode.substring(0, 2).toLowerCase() : languageCode;
        return Locale.of(lang);
    }
}
