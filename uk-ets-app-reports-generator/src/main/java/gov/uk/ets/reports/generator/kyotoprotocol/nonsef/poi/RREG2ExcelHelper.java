package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi;

import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r2itl.RREG2TransactionItem;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r2itl.UnitInvolved;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.util.ApplicationLabels;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.DateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Comparator;

/**
 * A helper that will keep the operations for RITL2 reports.
 *
 * @author gkountak
 */
public class RREG2ExcelHelper extends RREGExcelHelper {

    public RREG2ExcelHelper() {
        super();
    }

    /**
     * Add the headers.
     * @return
     */
    public void addHeaders() {
        Sheet sheet = getWorkbook().getSheetAt(0);
        createHeaderFirstRow(sheet);
        createHeaderSecondRow(sheet);
        mergeHeaderRowsCells(sheet);

        super.autoSizeColumns(sheet, 11);

    }

    private void mergeHeaderRowsCells(Sheet sheet) {
        sheet.addMergedRegion(new CellRangeAddress(6,7,0,0));
        sheet.addMergedRegion(new CellRangeAddress(6,6,1,2));
        sheet.addMergedRegion(new CellRangeAddress(6,7,3,3));
        sheet.addMergedRegion(new CellRangeAddress(6,7,4,4));
        sheet.addMergedRegion(new CellRangeAddress(6,7,5,5));
        sheet.addMergedRegion(new CellRangeAddress(6,7,6,6));
        sheet.addMergedRegion(new CellRangeAddress(6,7,7,7));
        sheet.addMergedRegion(new CellRangeAddress(6,6,8,10));
    }

    private void createHeaderSecondRow(Sheet sheet) {
        Row headerRow2 = sheet.createRow(sheet.getLastRowNum()+1);
        headerRow2.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
        Cell headerCell = headerRow2.createCell(1);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2ReportYear"));

        headerCell = headerRow2.createCell(2);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2PriorRepoYear"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(0);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(3);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(4);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(5);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(6);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(7);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(8);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2SerialNumber"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(9);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2UnitType"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow2.createCell(10);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2Quantity"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

    }

    private void createHeaderFirstRow(Sheet sheet) {
        Row headerRow = sheet.createRow(sheet.getLastRowNum()+1);
        headerRow.setHeightInPoints((4*sheet.getDefaultRowHeightInPoints()));
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2DesRespoCode"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow.createCell(1);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2AverNumOccurPerTrans"));

        headerCell = headerRow.createCell(2);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow.createCell(3);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2TransNum"));

        headerCell = headerRow.createCell(4);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2PropDateTime"));

        headerCell = headerRow.createCell(5);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2TransType"));

        headerCell = headerRow.createCell(6);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2FinalState"));

        headerCell = headerRow.createCell(7);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2Explanation"));

        headerCell = headerRow.createCell(8);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg2UnitsInvolved"));

        headerCell = headerRow.createCell(9);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow.createCell(10);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
    }

    public void addResponseCodeRow(String responseCode, double reportedYearTx, double priorToReportedYearTx) {
        int cellPos = 0;
        Sheet sheet = getWorkbook().getSheetAt(0);
        Row row = sheet.createRow(sheet.getLastRowNum()+1);
        Cell cell = row.createCell(cellPos++);
        cell.setCellStyle(getNormalStyle(sheet.getWorkbook()));
        cell.setCellValue(responseCode);

        cell = row.createCell(cellPos++);
        cell.setCellStyle(getNormal2DecimalStyle(sheet.getWorkbook()));
        cell = getCellTypeDouble(cell, getHalfEvenRound(reportedYearTx));

        cell = row.createCell(cellPos++);
        cell.setCellStyle(getNormal2DecimalStyle(sheet.getWorkbook()));
        cell = getCellTypeDouble(cell, getHalfEvenRound(priorToReportedYearTx));

        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 10));
    }

    /**
     * Add the transaction row.
     */
    public void addTransactionRow(RREG2TransactionItem info) {
        String txNumber = info.getTransactionIdentifier();
        String proposalDateTime = DateUtil.getDateAsString(info.getTransactionDate());
        String txType = getTxTypeLabel(info);
        String finalState = getFinalState(info.getTransactionStatus());
        Sheet sheet = getWorkbook().getSheetAt(0);
        int index = 0;
        info.getUnitsInvolved().sort(Comparator.comparing(UnitInvolved::getSerialNumber));
        for (UnitInvolved unit : info.getUnitsInvolved()) {
            int cellPos = 3;
            int rowPos = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowPos);

            Cell cell = row.createCell(cellPos++);
            cell.setCellValue(index == 0 ? txNumber : "");

            cell = row.createCell(cellPos++);
            cell.setCellValue(index == 0 ? proposalDateTime : "");

            cell = row.createCell(cellPos++);
            String transactionType = (!"".equals(txType) && txType!=null) ? txType : "";
            cell.setCellValue(index == 0 ? transactionType : "");

            cell = row.createCell(cellPos++);
            String state = (!"".equals(finalState) && finalState!=null) ? finalState : "";
            cell.setCellValue(index == 0 ? state : "");

            cellPos++;
            Cell unitCell = row.createCell(cellPos++);
            unitCell.setCellValue(unit.getSerialNumber());

            unitCell = row.createCell(cellPos++);
            unitCell.setCellValue(unit.getUnitType());

            unitCell = row.createCell(cellPos++);
            unitCell.setCellValue(unit.getQuantity());
            index++;
        }

    }

    private String getTxTypeLabel(RREG2TransactionItem item) {

        if (item.getTransactionType() == null ) {
            return " ";
        } else {
            return ApplicationLabels.get("transactionType." + item.getTransactionType());
        }
    }

    private String getFinalState(String status) {

        switch (status) {
            case "CANCELLED":
                return ApplicationLabels.get("transactionStatus.CANCELLED");
            case "TERMINATED":
                return ApplicationLabels.get("transactionStatus.TERMINATED");
            case "COMPLETED":
                return ApplicationLabels.get("transactionStatus.COMPLETED");
            default:
                return "";
        }
    }
}
