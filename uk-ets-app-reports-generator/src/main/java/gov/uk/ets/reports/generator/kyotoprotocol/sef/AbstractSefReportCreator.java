package gov.uk.ets.reports.generator.kyotoprotocol.sef;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.DBConnect;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.ConfigLoader;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.NotificationEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The abstract sef report creator
 *
 * @author kattoulp
 *
 */
public abstract class AbstractSefReportCreator {

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    ConfigLoader cl = ConfigLoader.getConfigLoader();


    /**
     * Creates the sef reports
     */
    public abstract KyotoReportOutcome [] createSefReports();

    /**
     * @param reportingYear
     * @param cl
     * @param query
     * @param reportEndDate
     * @return
     */
    List<NotificationEntry> fetchNotificationEntries(short reportingYear, ConfigLoader cl, String query, Date reportEndDate,
                                                     String jdbcUrl, String username, String password) {
        List<NotificationEntry> entries = new ArrayList<NotificationEntry>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConnect.getConnection(jdbcUrl, username, password)) {

            ps = con.prepareStatement(query);
            DBConnect.setFetchSize(ps, cl.getDbQueryFetchSize());
            ps.setDate(1, getReportingLimitDate(reportingYear, reportEndDate));
            rs = ps.executeQuery();

            while (rs.next()) {
                NotificationEntry entry = new NotificationEntry();

                entry.setNotificationId(rs.getString(1));
                entry.setRegistry(rs.getString(2));
                entry.setYear(rs.getShort(3));
                entry.setNotificationTypeCode(rs.getString(4));
                entry.setQuantity(rs.getLong(5));

                entries.add(entry);
            }
        } catch (SQLException e) {
            DBConnect.close(ps);
            DBConnect.close(rs);
            System.exit(-1);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return entries;
    }

    /**
     * Limit history to events (strictly) before 1st of January of reported year,
     * or the day following the end date of the report (SEFCOLLAB-422, swalense)
     * @param reportingYear the reporting year
     * @param reportEndDate the report end date
     * @return A date suitable for use in reporting queries
     */
    protected java.sql.Date getReportingLimitDate(short reportingYear, Date reportEndDate) {

        // Calendar is built for default timezone since that is what PreparedStatement setDate uses
        Calendar limit = Calendar.getInstance();
        if (reportEndDate == null) {
            limit.set(reportingYear+1, 0, 1, 0, 0, 0);
        } else {
            limit.setTime(reportEndDate);
            limit.add(Calendar.DAY_OF_MONTH, 1);
        }

        return new java.sql.Date(limit.getTimeInMillis());
    }
}

