package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author gkountak
 */
public class DateUtil {

    /**
     * The format that will be used.
     */
    public static final String UNFCCC_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Convert a {@link Date} to a {@link String} using the format: YYYY-MM-DD HH:MM:SS.
     *
     * @param d
     * @return
     */
    public static String getDateAsString(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat(UNFCCC_DATE_FORMAT);
        return sdf.format(d);
    }

    /**
     * Helper that returns the year of a {@link Date}.
     * @param date
     * @return
     */
    public static int getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int compareDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("both input dates must be not null");
        } else {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return cal1.compareTo(cal2);
        }
    }
}