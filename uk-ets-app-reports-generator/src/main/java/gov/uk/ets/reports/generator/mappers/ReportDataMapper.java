package gov.uk.ets.reports.generator.mappers;

import gov.uk.ets.reports.generator.domain.ReportData;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

public interface ReportDataMapper<T extends ReportData> {
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 7, true)
        .toFormatter();

    DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    DateTimeFormatter prettyDateFormatter =
        DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale.Builder().setLanguage("en").setRegion("EN").build());

    @Deprecated
    List<T> mapData(ReportCriteria criteria);

    List<T> mapData(ReportQueryInfoWithMetadata reportQueryInfo);

    /**
     * Parses string date representation from database to LocalDateTime.
     */
    default LocalDateTime parseDate(String date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * Parses string date representation from database to d MMMM yyyy format.
     */
    default String prettyDate(String date) {
        LocalDateTime localDateTime = parseDate(date);
        return prettyDate(localDateTime);
    }

    /**
     * Parses LocalDateTime to d MMMM yyyy format.
     */
    default String prettyDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return prettyDateFormatter.format(localDateTime);
    }

    /**
     * Returns empty string in case the value is null in the database.
     * This is needed because the getLong method of ResultSet returns 0 when the value is null.
     */
    default Long retrieveLong(ResultSet resultSet, int columnIndex) throws SQLException {
        long value = resultSet.getLong(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        }
        return value;
    }
}
