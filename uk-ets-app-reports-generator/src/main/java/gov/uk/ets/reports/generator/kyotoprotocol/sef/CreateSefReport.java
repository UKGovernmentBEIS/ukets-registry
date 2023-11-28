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
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.*;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1.AbstractTransactionProcessor;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1.AggregatorProcessor;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1.InitializeSubmissionsProcessor;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1.ProcessorFactory;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.ConfigLoader;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.FirstTrack2TransferEntry;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.NotificationEntry;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;
import lombok.extern.log4j.Log4j2;

/**
 * A sef cp1 report creator
 * @author gkountak
 */
@Log4j2
public class CreateSefReport extends AbstractSefReportCreator {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    /**
     * The main query file.
     */
    private static final String QUERY_FILE = "/int/unfccc/siar/reportgenerator/query/sef-main.sql";
    /**
     * The first track2 query file.
     */
    public static final String FIRST_TRACK2_TRANSFER_QUERY_FILE = "/int/unfccc/siar/reportgenerator/query/first-track2-transfer.sql";
    /**
     * The notification retrieval file.
     */
    public static final String NOTIFICATION_QUERY_FILE = "/int/unfccc/siar/reportgenerator/query/process-notification.sql";

    private short reportingYear;

    private short submissionYear;

    private short commitmentPeriod;

    private Date reportEndDate;

    public CreateSefReport(KyotoProtocolParams params) {
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
     * This function creates a report for a set of given registries, reporting year and reporting CP
     */
    public KyotoReportOutcome [] createSefReports() {
        Map<String, SEFSubmission> sefSubmissions = processMainSEFQuery(initiliazeSubmissions());
        processFirstTrack2TransferQuery(sefSubmissions);
        processNotificationsQuery(sefSubmissions);

        KyotoReportOutcome [] xmlFiles;
        if(cl.isInAggregatedMode()) {
            xmlFiles = printReports(aggregateReports(sefSubmissions));
        } else {
            xmlFiles = printReports(sefSubmissions);
        }
        return xmlFiles;
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
        for (Map.Entry<String,SEFSubmission> submissionEntry : sefSubmissions.entrySet()) {
            xsdSubs.add(submissionEntry.getValue());
        }

        log.debug("Conversion to SEF report format completed.");

        KyotoReportOutcome[] xmlFiles = CreateXML.createReports(xsdSubs);

        log.debug("Xml creation completed.");

        log.info("Report generation process completed.");
        log.info("Created " + xmlFiles.length + " SIAR reports in memory");

        return xmlFiles;
    }

    /**
     * Creates a map containing the {@link SEFSubmission} per registry.
     *
     * @param sefSubmissions
     * @return
     */
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
                    AbstractTransactionProcessor proc = ProcessorFactory.create(sefEntry.getTransactionTypeCode(), true);
                    setHeaderInformation(proc);
                    proc.process(sefSubmissions, sefEntry, ITLCommitmentPeriodEnum.getFromCode(commitmentPeriod),
                            reportingYear);
                }
                if (reportedRegistries.contains(sefEntry.getAcquiringRegistryCode()) && sefEntry.getTransactionTypeCode() == ITLTransactionTypeEnum.EXTERNAL_TRANSFER) {
                    // call Process External Transfer AR with SEFSubmission, reportYear, and entry
                    AbstractTransactionProcessor proc = ProcessorFactory.create(sefEntry.getTransactionTypeCode(), false);
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
        try (Connection con = DBConnect.getConnection(jdbcUrl, username, password)) {
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
                entry.setApplicablePeriodCode(ITLCommitmentPeriodEnum.getFromName(rs.getString(12)));
                entry.setAmount(rs.getLong(13));
                entry.setTrack(rs.getString(14));
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

    /**
     * Will update the Map of {@link SEFSubmission} by adding the first track2 transfer information.
     *
     * @param sefSubmissions
     */
    private void processFirstTrack2TransferQuery(Map<String, SEFSubmission> sefSubmissions) {

        QueryBuilder qb = new QueryBuilder();
        String query = "";
        try {
            query = qb.getQueryFromFileStripNewlines(FIRST_TRACK2_TRANSFER_QUERY_FILE);

            // log the query after replacing '?' chars with actual parameters.
            String queryToLog = qb.getQueryFromFile(FIRST_TRACK2_TRANSFER_QUERY_FILE)
                    .replaceFirst("\\?", String.valueOf(reportingYear))
                    .replaceFirst("\\?", getReportingLimitDate(reportingYear, reportEndDate).toString())
                    .replaceFirst("\\?", String.valueOf(commitmentPeriod));
            log.info("Query is: \n" + queryToLog);
        } catch (FileNotFoundException e) {
            log.fatal("Could not locate first track2 transfer query file.", e);
            System.exit(-1);
        }

        List<FirstTrack2TransferEntry> entries = fetchFirstTrack2TransferEntries(reportingYear, commitmentPeriod,
                reportEndDate, cl, query);

        try {
            log.debug("Start processing of First Track2 Transfer data (" + entries.size() + " sef entries)");
            Set<String> reportedRegistries = cl.getReportedRegistries();
            for (FirstTrack2TransferEntry entry : entries) {

                if (reportedRegistries.contains(entry.getRegistry())) {
                    AbstractTransactionProcessor proc = ProcessorFactory.createForFirstTrack2Trabnsfer();
                    setHeaderInformation(proc);
                    proc.process(sefSubmissions, entry, ITLCommitmentPeriodEnum.getFromCode(commitmentPeriod),	reportingYear);
                }
            }

            log.debug("Processing of First Track2 Transfer data completed.");
        } finally {
            log.debug("Logging of First Track2 Transfer data completed.");
        }

    }

    /**
     * @param reportingYear
     * @param commitmentPeriod
     * @param cl
     * @param query
     * @return
     */
    private List<FirstTrack2TransferEntry> fetchFirstTrack2TransferEntries(short reportingYear,
                                                                           short commitmentPeriod, Date reportEndDate, ConfigLoader cl, String query) {
        List<FirstTrack2TransferEntry> entries = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConnect.getConnection(jdbcUrl, username, password)) {
            ps = con.prepareStatement(query);
            DBConnect.setFetchSize(ps, cl.getDbQueryFetchSize());
            ps.setShort(1, reportingYear);
            ps.setDate(2, getReportingLimitDate(reportingYear, reportEndDate));
            ps.setString(3, String.format("CP%d",commitmentPeriod));
            rs = ps.executeQuery();

            log.debug("FirstTrack2Transfer query executed.");
            while (rs.next()) {
                FirstTrack2TransferEntry entry = new FirstTrack2TransferEntry();

                entry.setTransactionId(rs.getString(1));
                entry.setRegistry(rs.getString(2));
                entry.setQuantity(rs.getLong(3));

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


    /**
     * Will update the Map of {@link SEFSubmission} by adding the notification information.
     *
     * @param sefSubmissions
     */
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

        List<NotificationEntry> entries = fetchNotificationEntries(reportingYear, cl, query, reportEndDate, jdbcUrl, username, password);

        try {
            log.debug("Start processing of Notification data (" + entries.size() + " sef entries)");
            Set<String> reportedRegistries = cl.getReportedRegistries();
            for (NotificationEntry entry : entries) {
                if (reportedRegistries.contains(entry.getRegistry())) {
                    AbstractTransactionProcessor proc = ProcessorFactory.createForNotificationProcess();
                    setHeaderInformation(proc);
                    proc.process(sefSubmissions, entry, ITLCommitmentPeriodEnum.getFromCode(commitmentPeriod),	reportingYear);
                }
            }

            log.debug("Processing of Notification data completed.");
        } finally {
            log.debug("Logging of Notification data completed.");
        }

    }

    private void setHeaderInformation(AbstractTransactionProcessor proc) {
        proc.setReportYear(reportingYear);
        proc.setSubmitCP(commitmentPeriod);
        proc.setSubmitYear(submissionYear);
        proc.setReportEndDate(reportEndDate);
    }

}
