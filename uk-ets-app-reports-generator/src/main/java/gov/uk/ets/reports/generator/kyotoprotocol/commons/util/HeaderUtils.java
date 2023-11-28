package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Header;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SourceType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gkountak
 *
 */
public class HeaderUtils {

    /* Date format patterns for files with report end date. */
    private static final String SDF_REPORT_END_DATE_OUT_PATTERN = "yyyyMMdd";
    private static final String SDF_REPORT_END_DATE_IN_PATTERN = "yyyy-MM-dd";

    /**
     * Creates the concatenated file name from the specific Header.
     * @param header
     * @return
     */
    public static String concatFileName(Header header) throws ParseException {
        return getTypeForFileName(header.getSource())
                + "_" + header.getParty()
                + "_" + header.getReportedYear()
                + "_" + header.getCommitmentPeriod()
                + "_" + header.getVersion() + ""
                + (header.getReportEndDate() == null ?
                "" : "_" + HeaderUtils.formatSdfReportEndDateOut((HeaderUtils.parseSdfReportEndDateIn(header.getReportEndDate()))));
    }

    /**
     * Creates the concatenated file name from the specific Header.
     * @param header the sef 2.0 header
     * @return
     */
    public static String concatFileName(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Header header) throws ParseException {
        return getTypeForFileName(header.getSource())
                + "_" + header.getParty()
                + "_" + header.getReportedYear()
                + "_" + header.getCommitmentPeriod()
                + "_" + header.getVersion() + ""
                + (header.getReportEndDate() == null ?
                "" : "_" + HeaderUtils.formatSdfReportEndDateOut((HeaderUtils.parseSdfReportEndDateIn(header.getReportEndDate()))));
    }

    /**
     * Parses the provided report end date string representation
     * @param date The report end date
     * @return The parsed {@link Date} object
     * @throws ParseException
     */
    public static Date parseSdfReportEndDateIn(String date) throws ParseException{
        return new SimpleDateFormat(HeaderUtils.SDF_REPORT_END_DATE_IN_PATTERN).parse(date);
    }

    /**
     * Parses the provided report end date string representation
     * @param date The report end date
     * @return The parsed {@link Date} object
     * @throws ParseException
     */
    public static Date parseSdfReportEndDateOut(String date) throws ParseException{
        return new SimpleDateFormat(HeaderUtils.SDF_REPORT_END_DATE_OUT_PATTERN).parse(date);
    }

    /**
     * Formats the provided report end date
     * @param date The report end date
     * @return The formatted {@link Date} object
     */
    public static String formatSdfReportEndDateIn(Date date) {
        return new SimpleDateFormat(HeaderUtils.SDF_REPORT_END_DATE_IN_PATTERN).format(date);
    }

    /**
     * Formats the provided report end date
     * @param date The report end date
     * @return The formatted {@link Date} object
     */
    public static String formatSdfReportEndDateOut(Date date) {
        return new SimpleDateFormat(HeaderUtils.SDF_REPORT_END_DATE_OUT_PATTERN).format(date);
    }

    private static String getTypeForFileName(SourceType sourceType) {
        switch (sourceType) {
            case ITL:
                return "RITL1";
            case REG:
                return "RREG1";
        }
        return null;
    }


}
