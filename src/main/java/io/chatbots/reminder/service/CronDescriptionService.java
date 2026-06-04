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
import java.util.Locale;

@Service
public class CronDescriptionService {

    private static final Logger log = LoggerFactory.getLogger(CronDescriptionService.class);

    private static final CronParser QUARTZ_PARSER =
        new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));

    /**
     * Returns a human-readable schedule description, using the AI-provided one if available,
     * otherwise generating it from the cron expression or fireAt datetime.
     */
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
            var locale = resolveLocale(languageCode);
            return fireAt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale));
        } catch (Exception e) {
            return fireAt.toString();
        }
    }

    /** Computes the next fire time for a cron expression relative to a given timezone. */
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

    // cron-utils supports: EN, ES, PT, DE, FR, IT, ZH, KO, NL, AR, TR, PL
    // RU and UK fall back to English
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
}
