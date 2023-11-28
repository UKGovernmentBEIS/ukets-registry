package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r5itl;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome.KyotoReportContentType;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.DBConnect;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.constants.RGConstants;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREG5ExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi.RREGExcelHelper;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.protocol.NonSEFReport;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
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
public class REG5Report implements NonSEFReport {

    /**
     * Will create a report and then output the result on an excel file.
     */
    public KyotoReportOutcome createReport(short reportedYear, Set<String> parties, Set<String> discrResponseCodes,
                             KyotoProtocolParams params) {

        if (discrResponseCodes != null) {
            throw new IllegalArgumentException("The argument 'discrResponseCodes' is not supported for this report type");
        }

        KyotoReportOutcome report = null;
        try(Connection con = DBConnect.getConnection(params.getJdbcUrl(), params.getUsername(), params.getPassword())) {

            for (String registry : parties) {
                if (RGConstants.CDM.equals(registry)) {
                    log.info("Will not generate R5 report for registry: " + registry);
                    continue;
                }
                log.info("Generating R5 report for registry: " + registry);
                RREG5ExcelHelper excelUtil = new RREG5ExcelHelper();
                excelUtil.addTitle("RREG5 - Invalid units", registry, reportedYear);
                excelUtil.addHeaders();
                List<InvalidUnitLine> invalidUnitLines = calculateUnitConversionLines(registry, reportedYear, con);

                invalidUnitLines.forEach(excelUtil::addInvalidUnitsRow);

                report = KyotoReportOutcome
                    .builder()
                    .fileName("RREG5_" + registry + "_" + reportedYear + RREGExcelHelper.XLSX_SUFFIX)
                    .content(excelUtil.write())
                    .contentType(KyotoReportContentType.APPLICATION_VND_MS_EXCEL)
                    .build();
            }
        } catch (SQLException | IOException e) {
            log.fatal(e);
        }
        return report;
    }

    private List<InvalidUnitLine> calculateUnitConversionLines(String registry, short reportedYear, Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<InvalidUnitLine> invalidUnitsList = new ArrayList<>();
        try {
            ps = con.prepareStatement(REG5Queries.GET_INVALID_UNITS_LINE_QUERY);
            DBConnect.setFetchSizeToConfiguredValue(ps);
            ps.setString(1, registry);
            ps.setShort(2, reportedYear);
            rs = ps.executeQuery();
            while (rs.next()) {
                invalidUnitsList.add(InvalidUnitLine.builder()
                        .serialNumber(rs.getString("serial_number"))
                        .unitType(rs.getString("unit_type"))
                        .quantity(rs.getLong("quantity"))
                        .transactionNumber(rs.getString("transaction_number"))
                        .build());
            }

        } catch (SQLException e) {
            log.fatal(e);
        } finally {
            DBConnect.close(ps);
            DBConnect.close(rs);
        }
        return invalidUnitsList;
    }

}
