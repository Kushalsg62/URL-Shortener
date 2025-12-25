package com.kushalsg.urlshortener.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata"); // change to your timezone
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZONE);

    public static String format(Instant instant) {
        if (instant == null) return "N/A";
        return FORMATTER.format(instant);
    }
}
