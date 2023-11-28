package gov.uk.ets.reports.generator.export;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import gov.uk.ets.reports.model.ReportType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportFileGeneratorHelper {

    private static final DateTimeFormatter fileNameDateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final String REPORT_FILE_PREFIX = "uk_ets";
    private static final String REPORT_FILE_NAME_SEPARATOR = "_";

    /**
     * Generates the file name in the form: uk_ets_<code>type</code>_<code>requestDate</code>_xlsx.
     *
     * @param type        the report type
     * @param requestDate the report request date
     * @return the file name
     */
    public static String generateFileName(ReportType type, Long year, LocalDateTime requestDate) {
        return REPORT_FILE_PREFIX +
            REPORT_FILE_NAME_SEPARATOR +
            type.getName() + 
            Objects.toString(year,"") +
            REPORT_FILE_NAME_SEPARATOR +
            fileNameDateFormatter.format(requestDate) +
            "." +
            type.getFormat().getFileExtension();
    }
}
