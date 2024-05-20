package com.eripe14.houses.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class DurationUtil {

    private static final String PATTERN_FORMAT = "HH:mm dd.MM.yyyy";
    private static final String SCHEMATIC_PATTERN_FORMAT = "HH:mm_dd.MM.yyyy";

    private DurationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String format(Duration duration) {
        return format(duration, true);
    }

    public static String format(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Warsaw"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT);
        return formatter.format(zonedDateTime);
    }

    public static String formatSchematic(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Warsaw"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SCHEMATIC_PATTERN_FORMAT);
        return formatter.format(zonedDateTime);
    }

    public static String format(Duration duration, boolean removeMillis) {
        if (removeMillis) {
            duration = Duration.ofSeconds(duration.toSeconds());
        }

        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

}