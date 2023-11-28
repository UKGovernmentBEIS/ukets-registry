package gov.uk.ets.registry.api.transaction.common;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Class that provides utility methods for LocalDateTime objects' uses.
 */
public class LocalDateTimeUtils {

    /**
     * Constructor.
     */
    private LocalDateTimeUtils() {
        // nothing to implement here.
    }

    /**
     * Utility method that converts String to LocalDatetime object.
     *
     * @param input     the String input to be converted
     * @param formatter the String formatter for the convert
     * @return the produced LocalDateTime object, null if the input cannot be parsed to a LocalDateTime object.
     */
    public static LocalDateTime toDateTime(String input, String formatter) {

        LocalDateTime result;
        try {
            result = LocalDateTime.parse(input, DateTimeFormatter.ofPattern(formatter));
        } catch (DateTimeException | NullPointerException exception) {
            return null;
        }
        return result;
    }

    /**
     * Utility method that formats a {@link Date} object according to the {@link DateTimeFormatter#ISO_DATE_TIME} format.
     * @param input The {@link Date} input.
     * @return The {@link DateTimeFormatter#ISO_DATE_TIME} formatted string representation.
     */
    public static String format(Date input) {
        if (input == null) {
            return null;
        }
        return input.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * Utility method that determines if NOW date time is within limits.
     *
     * @param startDate the start date parameter
     * @param endDate   the end date parameter
     * @return true/false if parameter is/is not within period limits
     */
    public static boolean isWithinPeriodLimits(LocalDateTime startDate, LocalDateTime endDate) {

        LocalDateTime now = LocalDateTime.now();
        boolean value;

        if (startDate == null && endDate == null) {
            value = false;
        } else if (startDate == null) {
            value = now.isBefore(endDate);
        } else if (endDate == null) {
            value = now.isAfter(startDate);
        } else {
            value = now.isAfter(startDate) && now.isBefore(endDate);
        }
        return value;
    }
}
