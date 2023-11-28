package gov.uk.ets.registry.api.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

/**
 * Utilities.
 */
@Log4j2
public class Utils {

    private static final int MASK_LENGTH = 4;
    private static char MASK_CHAR = 'X';

    /**
     * Constructor.
     */
    private Utils() {
        // nothing to implement here.
    }

    /**
     * Checks whether the provided input string is a valid long number.
     * @param input The input string
     * @return false/true
     */
    public static boolean isLong(String input) {
        boolean result = true;
        try {
            Long.valueOf(input);
        } catch (NumberFormatException exc) {
            log.debug("The provided input {} is not a valid long number", input);
            result = false;
        }
        return result;
    }

    /**
     * Concatenates the provided input strings. Empty input strings are omitted.
     * @param delimiter The delimiter; a space is the default value
     * @param input The input strings
     * @return a string
     */
    public static String concat(String delimiter, String... input) {
        if (input == null || input.length == 0) {
            return "";
        }

        String separator = delimiter;
        if (!StringUtils.hasLength(separator)) {
            separator = " ";
        }

        StringJoiner joiner = new StringJoiner(separator);
        for (String element : input) {
            if (StringUtils.hasText(element)) {
                joiner.add(element);
            }
        }
        return joiner.toString();

    }

    /**
     * Converts the provided date to a formatted string.
     * @param date The input date.
     * @return a date formatted as string
     */
    public static String format(Date date) {
        String result = null;
        if (date != null) {
            result = new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
        }
        return result;
    }

    /**
     * Extracts the day formatted from the provided date.
     * Sample outputs:
     * <ul>
     *     <li>28 Sep 2020 (when fullMonth is false)</li>
     *     <li>28 September 2020 (when fullMonth is true)</li>
     * </ul>
     * @param date The date.
     * @param fullMonth Whether to display the full month.
     * @return the day.
     */
    public static String formatDay(LocalDateTime date, boolean fullMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fullMonth ? Constants.DAY_FORMAT_FULL_MONTH :
            Constants.DAY_FORMAT, Constants.LOCALE);
        return date.format(formatter);
    }

    /**
     * Extracts the day formatted from the provided date.
     * Sample output: 28 Sep 2020.
     * @param date The date.
     * @return the day.
     */
    public static String formatDay(LocalDateTime date) {
        return formatDay(date, false);
    }

    /**
     * Extracts the time formatted from the provided date.
     * @param date The date.
     * @return the time.
     */
    public static String formatTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT, Constants.LOCALE);
        return date.format(formatter).toLowerCase();
    }

    public static <T> String serialiseToJson(T value) {
        try {
            return new ObjectMapper().writeValueAsString(value);

        } catch (JsonProcessingException exc) {
            throw new IllegalStateException("Error during JSON serialisation", exc);
        }
    }

    public static <T> T deserialiseFromJson(String input, Class<T> type) {
        try {
            return new ObjectMapper().readValue(input, type);

        } catch (JsonProcessingException exc) {
            throw new IllegalStateException("Error during JSON serialisation", exc);
        }
    }

    public static <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    /**
     * Utility for masking identifier digits 3-6 of account full identifier.
     *
     * @param str input string
     * @return the masked fullIdentifier
     */
    public static String maskFullIdentifier(String str) {
        StringBuilder sbMaskString = new StringBuilder(MASK_LENGTH);

        for (int i = 0; i < MASK_LENGTH; i++) {
            sbMaskString.append(MASK_CHAR);
        }
        // Masking will start from the 3rd digit of identifier part
        int start = org.apache.commons.lang3.StringUtils.ordinalIndexOf(str, "-", 2) + (MASK_LENGTH - 1);

        return str.substring(0, start)
            + sbMaskString.toString()
            + str.substring(start + MASK_LENGTH);
    }
    
    public static String maskUserId(String userId) {
        return org.apache.commons.lang3.StringUtils.right(userId, 2);
    }
}
