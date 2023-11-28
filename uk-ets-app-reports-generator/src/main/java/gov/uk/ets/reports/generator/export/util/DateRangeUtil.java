package gov.uk.ets.reports.generator.export.util;

import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import lombok.experimental.UtilityClass;

/**
 * Utility class for setting the from/to report criteria.
 */
@UtilityClass
public class DateRangeUtil {

    /**
     * Helper that returns the 'from' value.
     * User defined or else 1/1/1900
     *
     * @param reportQueryInfo ReportQueryInfoWithMetadata
     * @return
     */
    public Date getFrom(ReportQueryInfoWithMetadata reportQueryInfo) {
        return reportQueryInfo.getFrom() != null ? reportQueryInfo.getFrom() :
            Date.from(LocalDate.of(1900, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    /**
     * Helper that returns the 'to' value.
     * User defined or else current date.
     *
     * @param reportQueryInfo ReportQueryInfoWithMetadata
     * @return
     */
    public Date getTo(ReportQueryInfoWithMetadata reportQueryInfo) {
        return reportQueryInfo.getTo() != null ? reportQueryInfo.getTo() :
            Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    public LocalDateTime getCutOffDateTime(ReportQueryInfoWithMetadata reportQueryInfo) {
        Instant instant = Instant.now();
        if (reportQueryInfo.getTo() != null && reportQueryInfo.getCutOffTime() != null) {
            String cutOffDate = new SimpleDateFormat("yyyy-MM-dd").format(reportQueryInfo.getTo());
            instant = Timestamp.valueOf(cutOffDate + " " + reportQueryInfo.getCutOffTime()).toInstant();
        } else if (reportQueryInfo.getTo() == null && reportQueryInfo.getCutOffTime() != null) {
            LocalDate date = LocalDate.now();
            instant = Timestamp.valueOf(date + " " + reportQueryInfo.getCutOffTime()).toInstant();
        } else if (reportQueryInfo.getTo() != null && reportQueryInfo.getCutOffTime() == null) {
            LocalTime time = LocalTime.now();
            String cutOffDate = new SimpleDateFormat("yyyy-MM-dd").format(reportQueryInfo.getTo());
            instant = Timestamp.valueOf(cutOffDate + " " + time).toInstant();
        }
        return LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.toString()));
    }
}
