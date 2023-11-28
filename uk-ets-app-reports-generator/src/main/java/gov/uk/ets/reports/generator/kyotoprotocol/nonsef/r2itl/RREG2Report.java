package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r2itl;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome.KyotoReportContentType;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.DBConnect;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREG2ExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREGExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.protocol.NonSEFReport;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author gkountak
 */
@Log4j2
public class RREG2Report implements NonSEFReport {

    /**
     * Multiplier for C-F columns
     */
    private static final double MULTIPLIER = 100000;

    /**
     * Will create a report and then output the result on an excel file.
     */
    public KyotoReportOutcome createReport(short reportedYear, Set<String> parties, Set<String> discrResponseCodes,
                             KyotoProtocolParams params) {

        KyotoReportOutcome report = null;
        try (Connection con = DBConnect.getConnection(params.getJdbcUrl(), params.getUsername(), params.getPassword())) {

            Map<String,Long> generalReportedYearCodeCount = getTransactionCountPerResponseMap(reportedYear, con);
            Map<String,Long>  registryTxCountReportedYear  = getRegistryTxCountReportedYearMap(reportedYear, con);
            Map<String,List<UnitInvolved>>  unitInvolvedTransactionMap  = getUnitInvolvedTransactionMap(reportedYear, con);
            Map<String, Map<String, Double>> responseCodeAverageReportedYearMap =
                    getResponseCodeAverageReportedYearMap(generalReportedYearCodeCount, registryTxCountReportedYear,
                            reportedYear, con);
            String registry = "GB";

            log.info("Generating R2 report for registry: " + registry);
            RREG2ExcelHelper excelUtil = new RREG2ExcelHelper();
            // create a new excel report
            excelUtil.addTitle("RREG2 - Discrepant transactions", registry, reportedYear);
            excelUtil.addHeaders();

            Long registryPreviousYear = getPerRegistryAndYear(reportedYear, registry, con);
            List<String> registryReportedYearCodeCountList = getResponseMapPerRegistryAndYear(reportedYear, registry,
                    con);
            for (String responseCode : registryReportedYearCodeCountList) {
                processResponseCode(responseCode, registryPreviousYear, reportedYear, registry, responseCodeAverageReportedYearMap,
                        excelUtil, unitInvolvedTransactionMap, con);
            }
            excelUtil.autoSizeColumns(excelUtil.getWorkbook().getSheetAt(0), 11);
            report = KyotoReportOutcome
                .builder()
                .fileName("RREG2_" + registry + "_" + reportedYear + RREGExcelHelper.XLSX_SUFFIX)
                .content(excelUtil.write())
                .contentType(KyotoReportContentType.APPLICATION_VND_MS_EXCEL)
                .build();

        } catch (Exception e) {
            log.fatal(e);
            log.error("Error create report RREG2: " + e.getMessage());
        }
        return report;
    }

    private void processResponseCode(String responseCode, Long registryPreviousYear, short reportedYear,
                                     String registry, Map<String, Map<String, Double>> responseCodeAverageReportedYearMap,
                                     RREG2ExcelHelper excelUtil, Map<String,List<UnitInvolved>>  unitInvolvedTransactionMap,
                                     Connection con){
        log.info("Processing response code: " + responseCode);

        Double registryReportedYearCodeAvg = responseCodeAverageReportedYearMap.get(responseCode).get(registry);
        Long registryPreviousYearsCodeCount = getPerRegistryYearAndResponseCode(reportedYear, Short.valueOf(responseCode),
                con);

        double registryPreviousYearCodeAvg = 0D;
        if (registryPreviousYear != 0) {
            registryPreviousYearCodeAvg = ((double) registryPreviousYearsCodeCount / (double) registryPreviousYear) * RREG2Report.MULTIPLIER;
        }

        excelUtil.addResponseCodeRow(responseCode, registryReportedYearCodeAvg, registryPreviousYearCodeAvg);

        // new line in excel file
        List<RREG2TransactionItem> txs = getTransactionsPerRegistryYearAndResponseCode(reportedYear,
                Short.valueOf(responseCode), con);
        for (RREG2TransactionItem item : txs) {
            log.info("Processing transaction: " + item.getTransactionIdentifier());
            item.setUnitsInvolved(unitInvolvedTransactionMap.get(item.getTransactionIdentifier()));
            excelUtil.addTransactionRow(item);
        }
    }


    private Map<String, Long> getTransactionCountPerResponseMap(short year, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Long> result = new HashMap<>();
        try {
            ps = con.prepareStatement(RREG2Queries.GET_GENERAL_REPORTED_YEAR_RESPONSE_COUNT);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setShort(1, year);
            rs = ps.executeQuery();

            while (rs.next()) {
                result.put(rs.getString(1), rs.getLong(2));
            }

        } catch (SQLException e) {
            log.fatal(e);
            log.error("Error create report RREG2: " + e.getMessage());
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return result;
    }

    /**
     * @return Map<String, Map<String, Long>> ResponseCodeAverageReportedYearMap
     *
     * The content is a second-level map named ReponseCodeRegistryAverageReportedYear.
     * There is an instance of ReponseCodeRegistryAverageReportedYear for each response code.
     * The key of this second-level map is the registry code
     *
     * The ResponseCodeAverageReportedYear is the whole Map which contains
     * the ReponseCodeRegistryAverageReportedYear Map<String, Map<String, Long>>.
     * @throws SQLException
     */
    private Map<String, Map<String, Double>> getResponseCodeAverageReportedYearMap(
            Map<String, Long> generalReportedYearCodeCount, Map<String, Long> registryTxCountReportedYear,
            short year, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (String responseCode : generalReportedYearCodeCount.keySet()) {
            Map<String, Double> responseCodeRegistryAverageReportedYearMap = new HashMap<>();
            for (Map.Entry<String, Long> item : registryTxCountReportedYear.entrySet()) {
                String registry = item.getKey();
                double txCount = item.getValue();
                try {

                    ps = con.prepareStatement(RREG2Queries.GET_COUNT);
                    DBConnect.setFetchSizeToConfiguredValue(ps);
                    ps.setString(1, registry);
                    ps.setInt(2, Integer.parseInt(responseCode));
                    ps.setShort(3, year);
                    rs = ps.executeQuery();

                    // query is expected to always return one and only one entry
                    rs.next();
                    double count = rs.getLong(1);
                    Double avg = txCount != 0 ? (count / txCount) * RREG2Report.MULTIPLIER : 0.0;
                    responseCodeRegistryAverageReportedYearMap.put(registry, avg);

                } catch (SQLException e) {
                    log.fatal(e);
                    log.error("Error create report RREG2: " + e.getMessage());
                    throw new SQLException();
                } finally {
                    DBConnect.close(ps);
                    DBConnect.close(rs);
                }
            }
            result.put(responseCode, responseCodeRegistryAverageReportedYearMap);
        }
        return result;
    }

    private  Map<String, Long> getRegistryTxCountReportedYearMap(short year, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Long> result = new HashMap<>();
        try {
            ps = con.prepareStatement(RREG2Queries.GET_REGISTRY_TX_COUNT_REPORTED_YEAR);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setShort(1, year);
            rs = ps.executeQuery();

            while (rs.next()) {
                result.put(rs.getString(1), rs.getLong(2));
            }

        } catch (SQLException e) {
            log.fatal(e);
            log.error("Error create report RREG2: " + e.getMessage());
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return result;
    }

    private  Map<String, List<UnitInvolved>> getUnitInvolvedTransactionMap(short year, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, List<UnitInvolved>> result = new HashMap<>();
        try {
            ps = con.prepareStatement(RREG2Queries.GET_UNITS_INVOLVED);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setShort(1, year);
            rs = ps.executeQuery();

            while (rs.next()) {
                String transactionIdentifier = rs.getString("transaction_identifier");
                UnitInvolved unit = UnitInvolved.builder()
                        .quantity(rs.getLong("quantity"))
                        .transactionIdentifier(transactionIdentifier)
                        .unitType(rs.getString("unit_type"))
                        .serialNumber(rs.getString("serial_number"))
                        .build();
                if (result.containsKey(transactionIdentifier)) {
                    result.get(transactionIdentifier).add(unit);
                } else {
                    List<UnitInvolved> units = new ArrayList<>();
                    units.add(unit);
                    result.put(transactionIdentifier, units);
                }
            }

        } catch (Exception e) {
            log.error("Error create report RREG2: " + e.getMessage());
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return result;
    }

    private Long getPerRegistryAndYear(short year, String registry, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        long count = 0L;
        try {
            ps = con.prepareStatement(RREG2Queries.GET_REGISTRY_PREVIOUS_YEAR);
            ps.setString(1, registry);
            ps.setShort(2, year);
            rs = ps.executeQuery();

            while (rs.next()) {
                count = rs.getLong(1);
            }

        } catch (SQLException e) {
            log.error("Error create report RREG2: " + e.getMessage());
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return count;
    }


    private List <String> getResponseMapPerRegistryAndYear(short year, String registry, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List <String> result = new ArrayList<>();
        try {
            ps = con.prepareStatement(RREG2Queries.GET_REGISTRY_REPORTED_YEAR_CODE_COUNT);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setString(1, registry);
            ps.setShort(2, year);
            rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getString(1));
            }

        } catch (SQLException e) {
            log.error("Error create report RREG2: " + e.getMessage());
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return result;
    }

    private Long getPerRegistryYearAndResponseCode(short year, Short responseCode, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        long count = 0L;
        try {
            ps = con.prepareStatement(RREG2Queries.GET_REGISTRY_PREVIOUS_YEARS_CODE_COUNT);
            ps.setShort(1, responseCode);
            ps.setShort(2, year);
            rs = ps.executeQuery();

            while (rs.next()) {
                count = rs.getLong(1);
            }

        } catch (SQLException e) {
            log.error("Error create report RREG2: " + e.getMessage());
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return count;
    }

    /**
     * Return a list with the transaction information.
     */
    private List<RREG2TransactionItem> getTransactionsPerRegistryYearAndResponseCode(short year, Short responseCode, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RREG2TransactionItem> list = new ArrayList<>();
        try {
            ps = con.prepareStatement(RREG2Queries.GET_LIST_OF_TRANSACTIONS_PROPOSED);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setShort(1, year);
            ps.setShort(2, responseCode);
            rs = ps.executeQuery();

            while (rs.next()) {
                RREG2TransactionItem item = new RREG2TransactionItem();
                item.setTransactionIdentifier(rs.getString("identifier"));
                item.setTransactionDate(rs.getTimestamp("message_date"));
                item.setTransactionType(rs.getString("type"));
                item.setTransactionStatus(rs.getString("status"));
                list.add(item);
            }

        } catch (SQLException e) {
            log.error("Error create report RREG2: " + e.getMessage());
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }

        return list;
    }

}
