package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r4itl;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome.KyotoReportContentType;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.DBConnect;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREG4ExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREGExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.protocol.NonSEFReport;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl.RREG3NotificationItem;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl.RREG3Report;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ReportTypeEnum;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static gov.uk.ets.reports.generator.kyotoprotocol.nonsef.constants.RGConstants.IMPENDING_EXPIRY_DESCRIPTION;

@Log4j2
public class RREG4Report implements NonSEFReport {

    /**
     * Will create a report and then output the result on an excel file.
     */
    public KyotoReportOutcome createReport(short reportedYear, Set<String> parties, Set<String> discrResponseCodes,
                             KyotoProtocolParams params) {

        KyotoReportOutcome report = null;
        var wrapper = new Object(){ Connection con = null; };
        try {
            wrapper.con = DBConnect.getConnection(params.getJdbcUrl(), params.getUsername(), params.getPassword());

            log.info("Report Type : " + ReportTypeEnum.R4REG.getReportType());
            log.info("Reported Year : " + reportedYear);

            String reportRegistryCode = "GB";

            RREG4ExcelHelper excelUtil = new RREG4ExcelHelper();
            excelUtil.addTitle("RREG4 – Non-Replacements", reportRegistryCode, reportedYear);
            excelUtil.addNotificationListHeaders();

            RREG3Report rreg3Report = new RREG3Report();
            Map<String, List<RREG3NotificationItem>> results = rreg3Report.getNotificationItemList(reportedYear, wrapper.con);

            results.forEach((notificationType, list) -> {
                excelUtil.addNotificationTypeDescription(notificationType);
                list.stream()
                        .filter(item ->Long.parseLong(item.getPostTargetDate()) > 0)
                        .forEach(excelUtil::addNotificationList);
            });
            results.forEach((notificationType, list) -> list.stream()
                        .filter(item ->Long.parseLong(item.getPostTargetDate()) > 0)
                        .forEach(item -> {
                            excelUtil.addNotificationHeaders();
                            excelUtil.addNotification(item, notificationType);
                            excelUtil.addUnitsHeaders();
                            List<UnitConcerned> units;
                            if (item.getNotificationTypeCode().equals(IMPENDING_EXPIRY_DESCRIPTION)) {
                                units = getITLUnits(Long.parseLong(item.getNotificationId()), wrapper.con);
                            } else {
                                units = getUnitsBlocksByProject(item.getProjectNumber(), wrapper.con);
                            }

                            units.forEach(excelUtil::addCellUnitItem);
                        })
            );

            report = KyotoReportOutcome
                .builder()
                .fileName("RREG4_" + reportRegistryCode + "_" + reportedYear + RREGExcelHelper.XLSX_SUFFIX)
                .content(excelUtil.write())
                .contentType(KyotoReportContentType.APPLICATION_VND_MS_EXCEL)
                .build();

        } catch (SQLException | IOException e) {
            log.error("Error create report RITL4: " + e);
        } finally {
            DBConnect.closeConnection(wrapper.con);
        }
        return report;
    }

    private static List<UnitConcerned> getITLUnits(Long notificationId, Connection con){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<UnitConcerned> results = new ArrayList<>();
        try {
            ps = con.prepareStatement(RREG4Queries.GET_UNITS_WHEN_TYPE_CODE_IS_3);
            ps.setLong(1, notificationId);
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(UnitConcerned.builder()
                        .unitType(rs.getString("unit_type"))
                        .quantity(rs.getLong("quantity"))
                        .serialNumber(rs.getString("serial_number"))
                        .build());
            }
        } catch (Exception e) {
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return results;
    }

    private static List<UnitConcerned> getUnitsBlocksByProject(String projectNumber, Connection con){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<UnitConcerned> results = new ArrayList<>();
        try {
            ps = con.prepareStatement(RREG4Queries.GET_UNITS_WHEN_TYPE_CODE_IS_NOT_3);
            ps.setString(1, projectNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(UnitConcerned.builder()
                        .unitType(rs.getString("unit_type"))
                        .quantity(rs.getLong("quantity"))
                        .serialNumber(rs.getString("serial_number"))
                        .build());
            }
        } catch (Exception e) {
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return results;
    }

}
