package io.chatbots.reminder.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Finds the nearest known timezone from GPS coordinates using a curated table of city coordinates.
 * More reliable than longitude→UTC-offset math which ignores DST and picks wrong continents.
 */
@Service
public class TimezoneGeoService {

    private record ZoneEntry(String zoneId, double lat, double lon) {}

    private static final List<ZoneEntry> ZONE_TABLE = List.of(
        // Americas
        new ZoneEntry("America/New_York",             40.7,  -74.0),
        new ZoneEntry("America/Chicago",              41.8,  -87.6),
        new ZoneEntry("America/Denver",               39.7, -104.9),
        new ZoneEntry("America/Los_Angeles",          34.0, -118.2),
        new ZoneEntry("America/Toronto",              43.7,  -79.4),
        new ZoneEntry("America/Vancouver",            49.2, -123.1),
        new ZoneEntry("America/Sao_Paulo",           -23.5,  -46.6),
        new ZoneEntry("America/Argentina/Buenos_Aires", -34.6, -58.4),
        new ZoneEntry("America/Mexico_City",          19.4,  -99.1),
        new ZoneEntry("America/Bogota",                4.7,  -74.1),
        new ZoneEntry("America/Lima",                -12.0,  -77.0),
        new ZoneEntry("America/Santiago",            -33.4,  -70.6),
        new ZoneEntry("America/Caracas",              10.5,  -66.9),
        new ZoneEntry("America/Halifax",              44.6,  -63.6),
        new ZoneEntry("America/Anchorage",            61.2, -149.9),
        new ZoneEntry("Pacific/Honolulu",             21.3, -157.8),
        // Europe
        new ZoneEntry("Europe/London",                51.5,   -0.1),
        new ZoneEntry("Europe/Lisbon",                38.7,   -9.1),
        new ZoneEntry("Europe/Dublin",                53.3,   -6.3),
        new ZoneEntry("Europe/Paris",                 48.8,    2.3),
        new ZoneEntry("Europe/Madrid",                40.4,   -3.7),
        new ZoneEntry("Europe/Rome",                  41.9,   12.5),
        new ZoneEntry("Europe/Berlin",                52.5,   13.4),
        new ZoneEntry("Europe/Amsterdam",             52.4,    4.9),
        new ZoneEntry("Europe/Zurich",                47.4,    8.5),
        new ZoneEntry("Europe/Vienna",                48.2,   16.4),
        new ZoneEntry("Europe/Prague",                50.1,   14.4),
        new ZoneEntry("Europe/Warsaw",                52.2,   21.0),
        new ZoneEntry("Europe/Stockholm",             59.3,   18.1),
        new ZoneEntry("Europe/Helsinki",              60.2,   25.0),
        new ZoneEntry("Europe/Riga",                  56.9,   24.1),
        new ZoneEntry("Europe/Kyiv",                  50.4,   30.5),
        new ZoneEntry("Europe/Minsk",                 53.9,   27.6),
        new ZoneEntry("Europe/Bucharest",             44.4,   26.1),
        new ZoneEntry("Europe/Athens",                37.9,   23.7),
        new ZoneEntry("Europe/Istanbul",              41.0,   29.0),
        new ZoneEntry("Europe/Moscow",                55.7,   37.6),
        // Africa
        new ZoneEntry("Africa/Casablanca",            33.6,   -7.6),
        new ZoneEntry("Africa/Algiers",               36.7,    3.0),
        new ZoneEntry("Africa/Lagos",                  6.5,    3.4),
        new ZoneEntry("Africa/Accra",                  5.6,   -0.2),
        new ZoneEntry("Africa/Cairo",                 30.0,   31.2),
        new ZoneEntry("Africa/Nairobi",               -1.3,   36.8),
        new ZoneEntry("Africa/Johannesburg",         -26.2,   28.0),
        // Middle East & Central Asia
        new ZoneEntry("Asia/Baghdad",                 33.3,   44.4),
        new ZoneEntry("Asia/Riyadh",                  24.7,   46.7),
        new ZoneEntry("Asia/Dubai",                   25.2,   55.3),
        new ZoneEntry("Asia/Tehran",                  35.7,   51.4),
        new ZoneEntry("Asia/Baku",                    40.4,   49.9),
        new ZoneEntry("Asia/Tbilisi",                 41.7,   44.8),
        new ZoneEntry("Asia/Jerusalem",               31.8,   35.2),
        new ZoneEntry("Asia/Tashkent",                41.3,   69.2),
        new ZoneEntry("Asia/Karachi",                 24.9,   67.0),
        new ZoneEntry("Asia/Kolkata",                 20.6,   78.9),
        new ZoneEntry("Asia/Dhaka",                   23.7,   90.4),
        new ZoneEntry("Asia/Yekaterinburg",           56.8,   60.6),
        // Asia-Pacific
        new ZoneEntry("Asia/Bangkok",                 13.8,  100.5),
        new ZoneEntry("Asia/Jakarta",                 -6.2,  106.8),
        new ZoneEntry("Asia/Singapore",                1.3,  103.8),
        new ZoneEntry("Asia/Hong_Kong",               22.3,  114.2),
        new ZoneEntry("Asia/Shanghai",                31.2,  121.5),
        new ZoneEntry("Asia/Taipei",                  25.0,  121.5),
        new ZoneEntry("Asia/Seoul",                   37.6,  127.0),
        new ZoneEntry("Asia/Tokyo",                   35.7,  139.7),
        new ZoneEntry("Asia/Novosibirsk",             55.0,   82.9),
        new ZoneEntry("Asia/Krasnoyarsk",             56.0,   92.9),
        new ZoneEntry("Asia/Irkutsk",                 52.3,  104.3),
        new ZoneEntry("Asia/Vladivostok",             43.1,  131.9),
        // Australia & Pacific
        new ZoneEntry("Australia/Perth",             -31.9,  115.9),
        new ZoneEntry("Australia/Adelaide",          -34.9,  138.6),
        new ZoneEntry("Australia/Brisbane",          -27.5,  153.0),
        new ZoneEntry("Australia/Melbourne",         -37.8,  145.0),
        new ZoneEntry("Australia/Sydney",            -33.9,  151.2),
        new ZoneEntry("Pacific/Auckland",            -36.9,  174.8),
        new ZoneEntry("Pacific/Fiji",                -18.1,  178.4),
        new ZoneEntry("UTC",                           0.0,    0.0)
    );

    public Optional<String> findTimezone(double lat, double lon) {
        return ZONE_TABLE.stream()
            .min((a, b) -> Double.compare(distSq(lat, lon, a.lat(), a.lon()),
                                          distSq(lat, lon, b.lat(), b.lon())))
            .map(ZoneEntry::zoneId);
    }

    private static double distSq(double lat1, double lon1, double lat2, double lon2) {
        double dlat = lat1 - lat2;
        double dlon = (lon1 - lon2) * Math.cos(Math.toRadians((lat1 + lat2) / 2));
        return dlat * dlat + dlon * dlon;
    }
}
