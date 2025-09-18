package gov.uk.ets.registry.api.common;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class DateUtils {

    private DateUtils() { 
    }

    public static Date parseIso(String strDate) {
        OffsetDateTime odt = OffsetDateTime.parse(strDate);
        Instant instant = odt.toInstant();
        return Date.from(instant);
    }

    public static String prettyCalendarDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("d MMMMM yyyy");
        return format.format(date);
    }

    public static String extractTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }
    
    /**
     * Formats the provided date as zoned time with timezone Europe/London.
     * 
     * @param date the date to format
     * @return the time formatted as HH:mm:ss (z)
     */
    public static String formatLondonZonedTime(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss (z)",Locale.UK);
        return date.toInstant().atZone(ZoneId.of("Europe/London")).format(formatter);
    }

    public static void validateFutureDate(Date date) {
        validateFutureDate(date.toInstant());
    }

    public static void validateFutureDate(Instant date) {
        if (date.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Date is in the past.");
        }
    }

    public static Date getMax(Date... dates) {
        return Stream.of(dates).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
    }
}
