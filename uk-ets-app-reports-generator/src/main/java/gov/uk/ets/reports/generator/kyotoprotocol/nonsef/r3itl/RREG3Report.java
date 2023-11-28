package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome.KyotoReportContentType;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.DBConnect;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREG3ExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREGExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.protocol.NonSEFReport;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ReportTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.DateUtil;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static gov.uk.ets.reports.generator.kyotoprotocol.nonsef.constants.RGConstants.*;

@Log4j2
public class RREG3Report implements NonSEFReport {

    /**
     * Will create a report and then output the result on an excel file.
     */
    public KyotoReportOutcome createReport(short reportedYear, Set<String> parties, Set<String> discrResponseCodes,
                              KyotoProtocolParams params) {
        Connection con = null;
        KyotoReportOutcome report = null;
        try {
            log.info("RREG3 Report starts...");

            log.info("Report Type \t: " + ReportTypeEnum.R3REG.getReportType());
            log.info("Reported Year \t: " + reportedYear);

            con = DBConnect.getConnection(params.getJdbcUrl(), params.getUsername(), params.getPassword());

            RREG3ExcelHelper excelUtil = new RREG3ExcelHelper();
            excelUtil.addTitle("RREG3 - CDM Notifications", "GB", reportedYear);
            excelUtil.addHeaders();

            Map<String, List<RREG3NotificationItem>> listMap = getNotificationItemList(reportedYear, con);

            for (Map.Entry<String,List<RREG3NotificationItem>> itemMap : listMap.entrySet()){
                excelUtil.addNotificationTypeDescr(itemMap.getKey());
                for (RREG3NotificationItem item :itemMap.getValue()){
                    excelUtil.addNotificationList(item);
                }
            }

            report = KyotoReportOutcome
                .builder()
                .fileName("RREG3_GB_" + reportedYear + RREGExcelHelper.XLSX_SUFFIX)
                .content(excelUtil.write())
                .contentType(KyotoReportContentType.APPLICATION_VND_MS_EXCEL)
                .build();

        } catch (SQLException | IOException e) {
            log.error("Error create report RREG3: " + e);
        } finally {
            DBConnect.closeConnection(con);
        }
        return report;
    }

    /**
     * Returns a list with the notification information.
     * @param registryNotificationDate
     * @param con
     * @return a list of RITL3NotificationItem
     */
    public Map<String, List<RREG3NotificationItem>> getNotificationItemList(short registryNotificationDate, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map <String, List<RREG3NotificationItem>> listMap = new HashMap<>();
        List<RREG3NotificationItem> results = new ArrayList<>();

        try {
            ps = con.prepareStatement(RREG3Queries.GET_NOTIFICATION_LIST_DURING_REPORT_YEAR);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setShort(1, registryNotificationDate);
            rs = ps.executeQuery();

            while (rs.next()) {
                RREG3NotificationItem item = new RREG3NotificationItem();

                List<RREG3TransactionTypeItem> rITL3TransTypeList =
                        getRelatedTransferQuantities(rs.getLong("notification_id"), con);

                if (!rITL3TransTypeList.isEmpty()){
                    for(RREG3TransactionTypeItem rITL3TransTypeItem : rITL3TransTypeList) {
                        populateNotificationItem(item, rITL3TransTypeItem, rs, con);
                    }
                } else{
                    populateNotificationItem(item, rs, con);
                }
                if (!results.contains(item)){
                    results.add(item);
                }
            }

            // Getting the list of RITL3NotificationItem for each Notification Type Code
            for (RREG3NotificationItem ritl3 :results){
                if (listMap.get(ritl3.getNotificationTypeCode()) == null) {
                    listMap.put(ritl3.getNotificationTypeCode(),
                            getListOfNotificationTypeCode(ritl3.getNotificationTypeCode(), results));
                }
            }

        } catch (Exception e) {
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }

        return listMap;
    }

    private void populateNotificationItem(RREG3NotificationItem item, ResultSet rs, Connection con) throws SQLException {
        Long notificationId = rs.getLong("notification_id");
        item.setNotificationTypeCode(notificationDescriptionMap().get(rs.getString("type")));
        item.setNotificationId(String.valueOf(notificationId));
        item.setNotificationIdentifier(String.valueOf(rs.getString("identifier")));
        item.setRegistryNotificationDate(DateUtil.getDateAsString(rs.getTimestamp("message_date")));
        item.setProjectNumber(rs.getString("project_number"));

        if (rs.getString("type").equals(IMPENDING_EXPIRY)) {
            item.setTargetValue(getITLTargetValue(notificationId, con));
        } else {
            item.setTargetValue(rs.getString("target_value"));
        }

        item.setActionDueDate(DateUtil.getDateAsString(rs.getTimestamp("action_due_date")));

        // Column 7 - At Target Date (Column 4 -  RITL3Queries.RESULT_AT_TARGET_DATE)
        Long targetValue = item.getTargetValue() != null && !"".equals(item.getTargetValue()) ? Long.parseLong(item.getTargetValue()) : 0L;
        Long targetDateValue = targetValue - getTransactionResultsAtTargetDate(notificationId, con);
        item.setTargetDate(String.valueOf(targetDateValue));
        // Column 8 - Post Target Date
        item.setPostTargetDate(calculatePostTargetDate(item));
    }

    private Map<String, String> notificationDescriptionMap() {
        Map<String, String> descriptionMap = new HashMap<>();
        descriptionMap.put(IMPENDING_EXPIRY, IMPENDING_EXPIRY_DESCRIPTION);
        descriptionMap.put(REVERSAL_OF_STORAGE, REVERSAL_OF_STORAGE_DESCRIPTION);
        descriptionMap.put(NON_SUBMISSION_OF_CERTIFICATION_REPORT, NON_SUBMISSION_OF_CERTIFICATION_REPORT_DESCRIPTION);
        return descriptionMap;
    }

    private void populateNotificationItem(RREG3NotificationItem item, RREG3TransactionTypeItem rITL3TransTypeItem,
                                             ResultSet rs, Connection con) throws SQLException {

        if (rITL3TransTypeItem.getTransactionTypeCode() != null) {
            // Column 5 - Cancellation
            if (Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "TransferToSOPforFirstExtTransferAAU") || // type 3
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "TransferToSOPForConversionOfERU") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "ExternalTransfer") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "ExternalTransferCP0") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "SurrenderKyotoUnits") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "ReversalSurrenderKyoto") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "SetAside") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "CancellationKyotoUnits") || // type 4
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "MandatoryCancellation") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "Art37Cancellation") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "CancellationAgainstDeletion") ||
                    Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "AmbitionIncreaseCancellation")){
                item.setCancelation(rITL3TransTypeItem.getQty().toString());
            }
            // Column 6 - Replacement
            if (Objects.equals(rITL3TransTypeItem.getTransactionTypeCode(), "Replacement")) {
                item.setReplacement(rITL3TransTypeItem.getQty().toString());
            }
        }

        populateNotificationItem(item, rs, con);
    }

    private List<RREG3NotificationItem> getListOfNotificationTypeCode(String notificationTypeCode , List<RREG3NotificationItem> results){
        return results.stream()
                .filter(item -> item.getNotificationTypeCode().equals(notificationTypeCode))
                .collect(Collectors.toList());
    }

    /**
     * Get the target number of units
     * @param notificationId
     * @param con
     * @return targetNumberOfUnits
     */
    private static String getITLTargetValue(Long notificationId, Connection con){
        int result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(RREG3Queries.GET_SUM_WHEN_TYPE_CODE_IS_3);
            ps.setLong(1, notificationId);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return String.valueOf(result);
    }

    /**
     * Get the the result of Column 5 Transaction type code
     * @param notificationId
     * @param con
     * @return transactiontypeCode
     */
    private static List<RREG3TransactionTypeItem> getRelatedTransferQuantities(Long notificationId, Connection con){
        List<RREG3TransactionTypeItem> result= new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = con.prepareStatement(RREG3Queries.DETERMINE_THE_RELATED_TRANSFERRED_QUANTITIES);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setLong(1, notificationId);
            rs = ps.executeQuery();
            while (rs.next()) {
                RREG3TransactionTypeItem rITL3TransactionTypeItem = new RREG3TransactionTypeItem();
                rITL3TransactionTypeItem.setTransactionTypeCode(rs.getString("type"));
                rITL3TransactionTypeItem.setQty(rs.getInt("qty"));
                result.add(rITL3TransactionTypeItem);
            }
        }catch (Exception e) {
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return result;
    }

    /**
     *  Calculation of Target Date - Column 7
     * @param notificationId
     * @param con
     * @return postTargetDate
     */
    private static Long getTransactionResultsAtTargetDate(Long notificationId, Connection con) {
        Long result=(long) 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = con.prepareStatement(RREG3Queries.RESULT_AT_TARGET_DATE);
            ps.setLong(1, notificationId);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getLong(1);
            }
        }catch (Exception e) {
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return result;
    }

    /**
     *  Target value – (cancellation + Replacement)
     * @param item
     * @return Calculation of Post Target Date - Column 8
     */
    private static String calculatePostTargetDate (RREG3NotificationItem item) {

        long targetValue = item.getTargetValue() == null ? 0 : Long.parseLong(item.getTargetValue());
        long cancellation = item.getCancelation() == null ? 0 : Long.parseLong(item.getCancelation());
        long replacement = item.getReplacement() == null ? 0 : Long.parseLong(item.getReplacement());

        long result = targetValue - (cancellation + replacement);

        return String.valueOf(result);
    }

}
