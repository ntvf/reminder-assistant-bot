package io.chatbots.reminder.bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TimezoneDetectorTest {

    @ParameterizedTest
    @CsvSource({
        "de, Europe/Berlin",
        "fr, Europe/Paris",
        "ru, Europe/Moscow",
        "es, Europe/Madrid",
        "cs, Europe/Prague",
        "pl, Europe/Warsaw",
        "uk, Europe/Kiev",
        "ja, Asia/Tokyo",
        "zh, Asia/Shanghai",
        "zh-CN, Asia/Shanghai",
        "de-AT, Europe/Berlin",
        "pt, Europe/Lisbon",
        "it, Europe/Rome",
        "nl, Europe/Amsterdam",
        "tr, Europe/Istanbul",
        "ko, Asia/Seoul",
        "ar, Asia/Riyadh",
        "hi, Asia/Kolkata",
        "vi, Asia/Ho_Chi_Minh"
    })
    void knownLanguages_mapToCorrectTimezone(String languageCode, String expectedTimezone) {
        assertThat(TimezoneDetector.detect(languageCode)).isEqualTo(expectedTimezone);
    }

    @Test
    void english_returnsUTC() {
        assertThat(TimezoneDetector.detect("en")).isEqualTo("UTC");
    }

    @Test
    void unknown_returnsUTC() {
        assertThat(TimezoneDetector.detect("xx")).isEqualTo("UTC");
    }

    @Test
    void null_returnsUTC() {
        assertThat(TimezoneDetector.detect(null)).isEqualTo("UTC");
    }

    @Test
    void blank_returnsUTC() {
        assertThat(TimezoneDetector.detect("")).isEqualTo("UTC");
    }

    @Test
    void subtag_strippedBeforeMatch() {
        assertThat(TimezoneDetector.detect("fr-FR")).isEqualTo("Europe/Paris");
        assertThat(TimezoneDetector.detect("ru_RU")).isEqualTo("Europe/Moscow");
    }
}
