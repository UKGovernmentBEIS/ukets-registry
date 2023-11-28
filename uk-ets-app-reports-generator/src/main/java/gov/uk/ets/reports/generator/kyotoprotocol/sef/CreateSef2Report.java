package gov.uk.ets.reports.generator.kyotoprotocol.sef;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.CreateXML;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.DBConnect;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.QueryBuilder;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.*;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2.*;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.*;
import lombok.extern.log4j.Log4j2;

/**
 * A sef cp2 report creator
 *
 * @author kattoulp
 *
 */
@Log4j2
public class CreateSef2Report extends AbstractSefReportCreator {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    /**
     * The main query file.
     */
    private static final String QUERY_FILE = "/int/unfccc/siar/reportgenerator/query/sef-cp2-main.sql";

    /**
     * The notification retrieval file.
     */
    public static final String NOTIFICATION_QUERY_FILE = "/int/unfccc/siar/reportgenerator/query/process-notification-cp2.sql";

    /**
     * The expired in holding accounts query file.
     */
    public static final String EXPIRED_IN_HOLDING_ACCOUNTS_QUERY_FILE = "/int/unfccc/siar/reportgenerator/query/process-expired-in-holding-accounts.sql";


    private final short reportingYear;

    private final short submissionYear;

    private short commitmentPeriod;

    private final Date reportEndDate;

    public CreateSef2Report(KyotoProtocolParams params) {
        super();
        this.reportingYear = params.getReportedYear();
        this.submissionYear = params.getSubmissionYear();
        if(params.getCommitmentPeriod().isPresent()) {
            this.commitmentPeriod = params.getCommitmentPeriod().get().shortValue();            
        }
        this.reportEndDate = params.getReportEndDate();
        this.jdbcUrl = params.getJdbcUrl();
        this.username = params.getUsername();
        this.password = params.getPassword();
    }

    /**
     * This function creates a report for a set of given registries, reporting
     * year and reporting CP
     */
    public KyotoReportOutcome [] createSefReports() {
        Map<String, SEFSubmission> sefSubmissions = processMainSEFQuery(initiliazeSubmissions());
        processNotificationsQuery(sefSubmissions);
        processExpiredInHoldingAccountsQuery(sefSubmissions);

        KyotoReportOutcome [] xmlFiles;
        if(cl.isInAggregatedMode()) {
            xmlFiles = printReports(aggregateReports(sefSubmissions));
        } else {
            xmlFiles = printReports(sefSubmissions);
        }
        return xmlFiles;
    }

    /**
     * Used to calculate the requirements for Expired in Holding Accounts in Table 3.
     *
     * @param sefSubmissions
     */
    private void processExpiredInHoldingAccountsQuery(Map<String, SEFSubmission> sefSubmissions) {
        QueryBuilder qb = new QueryBuilder();
        String query = "";
        try {
            query = qb.getQueryFromFileStripNewlines(EXPIRED_IN_HOLDING_ACCOUNTS_QUERY_FILE);

            // log the query after replacing '?' chars with actual parameters.
            String queryToLog = qb.getQueryFromFile(EXPIRED_IN_HOLDING_ACCOUNTS_QUERY_FILE)
                    .replaceFirst("\\?", getReportingLimitDate(reportingYear, reportEndDate).toString());

            log.info("Query is: \n" + queryToLog);
        } catch (FileNotFoundException e) {
            log.fatal("Could not locate expired in holding accounts query file.", e);
            System.exit(-1);
        }

        List<UnitBlockEntry> entries = fetchUnitBlockEntries(reportingYear, reportEndDate, cl, query);
        try {
            log.debug("Start processing of expired unit block entries (" + entries.size() + " entries)");
            Set<String> reportedRegistries = cl.getReportedRegistries();
            for (UnitBlockEntry entry : entries) {
                if (reportedRegistries.contains(entry.getRegistry())) {
                    ExpiredToHoldingAccountProcessor proc = ProcessorFactory.createForExpiredInHoldingAccountsProcess();
                    setHeaderInformation(proc);
                    proc.process(sefSubmissions, entry);
                }
            }

            log.debug("Processing of expired blocks data completed.");
        } finally {
            log.debug("Logging of expired blocks data completed.");
        }

    }

    /**
     * @param reportingYear
     * @param cl
     * @param query
     * @return
     */
    private List<UnitBlockEntry> fetchUnitBlockEntries(short reportingYear, Date reportEndDate, ConfigLoader cl, String query) {
        List<UnitBlockEntry> entries = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConnect.getConnection(jdbcUrl, username, password)) {
            ps = con.prepareStatement(query);
            DBConnect.setFetchSize(ps, cl.getDbQueryFetchSize());
            ps.setDate(1, getReportingLimitDate(reportingYear, reportEndDate));
            rs = ps.executeQuery();

            log.debug("fetch unit block entries query executed.");
            while (rs.next()) {
                UnitBlockEntry entry = new UnitBlockEntry();

                entry.setUnitTypeCode(ITLUnitTypeEnum.getFromName(rs.getString(1)));
                entry.setRegistry(rs.getString(2));
                entry.setExpiredYear(rs.getShort(3));
                entry.setAmount(rs.getLong(4));

                entries.add(entry);
            }
        } catch (SQLException e) {
            log.fatal("Could not execute query.", e);
            DBConnect.close(ps);
            DBConnect.close(rs);
            System.exit(-1);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return entries;
    }

    private Map<String, SEFSubmission> initiliazeSubmissions() {
        Map<String, SEFSubmission> submissions = new HashMap<>();
        for (String reportedRegistry : cl.getReportedRegistries()) {
            submissions.put(reportedRegistry, new SEFSubmission());
        }
        InitializeSubmissionsProcessor proc = new InitializeSubmissionsProcessor();
        setHeaderInformation(proc);
        return proc.process(submissions);
    }


    private Map<String, SEFSubmission> aggregateReports(Map<String, SEFSubmission> sefSubmissions) {
        AggregatorProcessor aggregator = ProcessorFactory.createForAggregation();
        setHeaderInformation(aggregator);
        return aggregator.aggregate(sefSubmissions, cl.getAggregatedGroup());
    }

    /**
     * Prints the reports.
     *
     * @param sefSubmissions
     */
    private KyotoReportOutcome [] printReports(Map<String, SEFSubmission> sefSubmissions) {
        Collection<SEFSubmission> xsdSubs = new ArrayList<>();
        for (Map.Entry<String, SEFSubmission> submissionEntry : sefSubmissions.entrySet()) {
            xsdSubs.add(submissionEntry.getValue());
        }

        log.debug("Conversion to SEF report format completed.");

        KyotoReportOutcome [] xmlFiles = CreateXML.createSef2Reports(xsdSubs);

        log.debug("Xml creation completed.");

        log.info("Report generation process completed.");
        log.info("Created " + xmlFiles.length + " SIAR reports in memory.");
        return xmlFiles;
    }


    private void processNotificationsQuery(Map<String, SEFSubmission> sefSubmissions) {
        QueryBuilder qb = new QueryBuilder();
        String query = "";
        try {
            query = qb.getQueryFromFileStripNewlines(NOTIFICATION_QUERY_FILE);

            // log the query after replacing '?' chars with actual parameters.
            String queryToLog = qb.getQueryFromFile(NOTIFICATION_QUERY_FILE)
                    .replaceFirst("\\?", getReportingLimitDate(reportingYear, reportEndDate).toString());

            log.info("Query is: \n" + queryToLog);
        } catch (FileNotFoundException e) {
            log.fatal("Could not locate notification query file.", e);
            System.exit(-1);
        }

        List<Cp2NotificationEntry> entries = fetchCp2NotificationEntries(reportingYear, reportEndDate, cl, query);

        try {
            log.debug("Start processing of Notification data (" + entries.size() + " sef entries)");
            Set<String> reportedRegistries = cl.getReportedRegistries();
            for (NotificationEntry entry : entries) {
                if (reportedRegistries.contains(entry.getRegistry())) {
                    AbstractTransactionProcessor proc = ProcessorFactory.createForNotificationProcess();
                    setHeaderInformation(proc);
                    proc.process(sefSubmissions, entry, ITLCommitmentPeriodEnum.getFromCode(commitmentPeriod),
                            reportingYear);
                }
            }

            log.debug("Processing of Notification data completed.");
        } finally {
            log.debug("Logging of Notification data completed.");
        }

    }

    /**
     * @param reportingYear
     * @param cl
     * @param query
     * @return
     */
    private List<Cp2NotificationEntry> fetchCp2NotificationEntries(short reportingYear, Date reportEndDate, ConfigLoader cl, String query) {
        List<Cp2NotificationEntry> entries = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConnect.getConnection(jdbcUrl, username, password)) {

            ps = con.prepareStatement(query);
            DBConnect.setFetchSize(ps, cl.getDbQueryFetchSize());
            ps.setDate(1, getReportingLimitDate(reportingYear, reportEndDate));
            rs = ps.executeQuery();

            log.debug("Notification query executed.");
            while (rs.next()) {
                Cp2NotificationEntry entry = new Cp2NotificationEntry();

                entry.setNotificationId(rs.getString(1));
                entry.setRegistry(rs.getString(2));
                entry.setYear(rs.getShort(3));
                entry.setNotificationTypeCode(rs.getString(4));
                if(entry.getITLNotificationTypeCode() != ITLNotificationTypeEnum.IMPENDING_TCER_LCER_EXPIRY) {
                    entry.setQuantity(rs.getLong(5));
                } else {
                    entry.setQuantity(rs.getLong(7));
                }
                entry.setUnitTypeCode(ITLUnitTypeEnum.getFromName(rs.getString(6)));

                entries.add(entry);
            }
        } catch (SQLException e) {
            log.fatal("Could not execute query.", e);
            DBConnect.close(ps);
            DBConnect.close(rs);
            System.exit(-1);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return entries;
    }


    private Map<String, SEFSubmission> processMainSEFQuery(Map<String, SEFSubmission> sefSubmissions) {
        QueryBuilder qb = new QueryBuilder();
        String query = "";
        try {
            query = qb.getQueryFromFileStripNewlines(QUERY_FILE);

            // log the query after replacing '?' chars with actual parameters.
            String queryToLog = qb.getQueryFromFile(QUERY_FILE)
                    .replaceFirst("\\?", String.valueOf(commitmentPeriod))
                    .replaceFirst("\\?", getReportingLimitDate(reportingYear, reportEndDate).toString());
            log.info("Query is: \n" + queryToLog);
        } catch (FileNotFoundException e) {
            log.fatal("Could not locate query file.", e);
            System.exit(-1);
        }

        List<SEFEntry> sefEntries = fetchSefEntries(reportingYear, commitmentPeriod, reportEndDate, cl, query);

        try {
            log.debug("Start processing of SEF data (" + sefEntries.size() + " sef entries)");
            Set<String> reportedRegistries = cl.getReportedRegistries();
            for (SEFEntry sefEntry : sefEntries) {
                if (reportedRegistries.contains(sefEntry.getTransferringRegistryCode())) {
                    AbstractTransactionProcessor proc = ProcessorFactory
                            .create(sefEntry.getTransactionTypeCode(), true);
                    setHeaderInformation(proc);
                    proc.process(sefSubmissions, sefEntry, ITLCommitmentPeriodEnum.getFromCode(commitmentPeriod),
                            reportingYear);
                }
                if (reportedRegistries.contains(sefEntry.getAcquiringRegistryCode())
                        && sefEntry.getTransactionTypeCode() == ITLTransactionTypeEnum.EXTERNAL_TRANSFER) {
                    // call Process External Transfer AR with SEFSubmission,
                    // reportYear, and entry
                    AbstractTransactionProcessor proc = ProcessorFactory.create(sefEntry.getTransactionTypeCode(),
                            false);
                    setHeaderInformation(proc);
                    proc.process(sefSubmissions, sefEntry, ITLCommitmentPeriodEnum.getFromCode(commitmentPeriod),
                            reportingYear);
                }
            }

            log.debug("Processing of SEF data completed.");
        } finally {
            log.debug("Logging of SEF data completed.");
        }
        return sefSubmissions;
    }

    private void setHeaderInformation(AbstractTransactionProcessor proc) {
        proc.setReportYear(reportingYear);
        proc.setSubmitCP(commitmentPeriod);
        proc.setSubmitYear(submissionYear);
        proc.setReportEndDate(reportEndDate);
    }


    /**
     * @param reportingYear
     * @param commitmentPeriod
     * @param cl
     * @param query
     * @return
     */
    private List<SEFEntry> fetchSefEntries(short reportingYear, short commitmentPeriod, Date reportEndDate, ConfigLoader cl, String query) {
        List<SEFEntry> sefEntries = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConnect.getConnection(jdbcUrl, username, password)){

            ps = con.prepareStatement(query);
            DBConnect.setFetchSize(ps, cl.getDbQueryFetchSize());
            ps.setString(1, String.format("CP%d",commitmentPeriod));
            ps.setDate(2, getReportingLimitDate(reportingYear, reportEndDate));
            rs = ps.executeQuery();

            log.debug("SEF query executed.");
            while (rs.next()) {
                SEFEntry entry = new SEFEntry();

                entry.setTransactionId(rs.getString(1));
                entry.setTransactionStatusDatetime(new java.util.Date(rs.getDate(2).getTime()));
                entry.setTransactionTypeCode(ITLTransactionTypeEnum.getFromCode(rs.getString(3)));
                entry.setTransferringRegistryCode(rs.getString(4));
                entry.setAcquiringRegistryCode(rs.getString(5));
                entry.setTransferringAccountType(ITLAccountTypeEnum.getFromName(rs.getString(6)));
                entry.setAcquiringAccountType(ITLAccountTypeEnum.getFromName(rs.getString(7)));
                entry.setNotificationTypeCode(ITLNotificationTypeEnum.getFromCode(rs.getString(8)));
                entry.setUnitTypeCode(ITLUnitTypeEnum.getFromName(rs.getString(9)));
                entry.setBlockLulucfCode(ITLLulucfTypeEnum.getFromCode(rs.getString(10)));
                entry.setNotifLulucfCode(ITLLulucfTypeEnum.getFromCode(rs.getString(11)));
                entry.setOriginalPeriodCode(ITLCommitmentPeriodEnum.getFromName(rs.getString(12)));
                entry.setApplicablePeriodCode(ITLCommitmentPeriodEnum.getFromName(rs.getString(13)));
                entry.setAmount(rs.getLong(14));
                entry.setTrack(rs.getString(15));
                entry.setExpiryDate(rs.getDate(16) == null ? null : new java.util.Date(rs.getDate(16).getTime()));
                entry.setFirstAauTransferFlag(rs.getString(17));
                sefEntries.add(entry);
            }
        } catch (SQLException e) {
            log.fatal("Could not execute query.", e);
            DBConnect.close(ps);
            DBConnect.close(rs);
            System.exit(-1);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return sefEntries;
    }

}
