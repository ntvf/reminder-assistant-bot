package io.chatbots.reminder.bot;

import java.util.Map;

public final class TimezoneDetector {

    private static final Map<String, String> LANGUAGE_TO_TIMEZONE = Map.ofEntries(
        Map.entry("de", "Europe/Berlin"),
        Map.entry("fr", "Europe/Paris"),
        Map.entry("es", "Europe/Madrid"),
        Map.entry("ru", "Europe/Moscow"),
        Map.entry("cs", "Europe/Prague"),
        Map.entry("pl", "Europe/Warsaw"),
        Map.entry("uk", "Europe/Kiev"),
        Map.entry("tr", "Europe/Istanbul"),
        Map.entry("ja", "Asia/Tokyo"),
        Map.entry("ko", "Asia/Seoul"),
        Map.entry("zh", "Asia/Shanghai"),
        Map.entry("ar", "Asia/Riyadh"),
        Map.entry("pt", "Europe/Lisbon"),
        Map.entry("it", "Europe/Rome"),
        Map.entry("nl", "Europe/Amsterdam"),
        Map.entry("sv", "Europe/Stockholm"),
        Map.entry("nb", "Europe/Oslo"),
        Map.entry("da", "Europe/Copenhagen"),
        Map.entry("fi", "Europe/Helsinki"),
        Map.entry("el", "Europe/Athens"),
        Map.entry("ro", "Europe/Bucharest"),
        Map.entry("hu", "Europe/Budapest"),
        Map.entry("sk", "Europe/Bratislava"),
        Map.entry("hr", "Europe/Zagreb"),
        Map.entry("bg", "Europe/Sofia"),
        Map.entry("sr", "Europe/Belgrade"),
        Map.entry("he", "Asia/Jerusalem"),
        Map.entry("fa", "Asia/Tehran"),
        Map.entry("th", "Asia/Bangkok"),
        Map.entry("vi", "Asia/Ho_Chi_Minh"),
        Map.entry("id", "Asia/Jakarta"),
        Map.entry("ms", "Asia/Kuala_Lumpur"),
        Map.entry("hi", "Asia/Kolkata"),
        Map.entry("lt", "Europe/Vilnius"),
        Map.entry("lv", "Europe/Riga"),
        Map.entry("et", "Europe/Tallinn"),
        Map.entry("ka", "Asia/Tbilisi"),
        Map.entry("az", "Asia/Baku"),
        Map.entry("hy", "Asia/Yerevan"),
        Map.entry("kk", "Asia/Almaty"),
        Map.entry("uz", "Asia/Tashkent"),
        Map.entry("mn", "Asia/Ulaanbaatar"),
        Map.entry("be", "Europe/Minsk")
    );

    private TimezoneDetector() {}

    public static String detect(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) return "UTC";
        var lang = languageCode.split("[-_]")[0].toLowerCase();
        return LANGUAGE_TO_TIMEZONE.getOrDefault(lang, "UTC");
    }
}
