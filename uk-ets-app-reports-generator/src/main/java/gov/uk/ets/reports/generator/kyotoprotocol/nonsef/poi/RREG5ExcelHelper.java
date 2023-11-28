package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi;

import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r5itl.InvalidUnitLine;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.util.ApplicationLabels;
import org.apache.poi.ss.usermodel.*;


/**
 * A helper that will keep the operations for R5REG reports.
 *
 * @author gkountak
 */
public class RREG5ExcelHelper extends RREGExcelHelper {

    public RREG5ExcelHelper() {
        super();
    }

    public void addHeaders() {
        Sheet sheet = getWorkbook().getSheetAt(0);
        Row headerRow = sheet.createRow(sheet.getLastRowNum()+1);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg5SerialNumber"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));

        headerCell = headerRow.createCell(1);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg5UnitType"));

        headerCell = headerRow.createCell(2);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg5Quantity"));

        headerCell = headerRow.createCell(3);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        headerCell.setCellValue(ApplicationLabels.get("label.rreg5TransactionNumber"));

        autoSizeColumns(sheet, 4);
    }

    public void addInvalidUnitsRow(InvalidUnitLine line) {
        Sheet sheet = getWorkbook().getSheetAt(0);
        Row row = sheet.createRow(sheet.getLastRowNum()+1);
        Cell cell = row.createCell(0);
        cell.setCellStyle(getNormalStyle(sheet.getWorkbook()));
        cell.setCellValue(line.getSerialNumber());

        cell = row.createCell(1);
        cell.setCellStyle(getNormalStyle(sheet.getWorkbook()));
        cell.setCellValue(line.getUnitType());

        cell = row.createCell(2);
        cell.setCellStyle(getNormalStyle(sheet.getWorkbook()));
        cell.setCellValue(line.getQuantity());
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);

        cell = row.createCell(3);
        cell.setCellStyle(getNormalCommaSeperatedStyle(sheet.getWorkbook()));
        cell.setCellValue(line.getTransactionNumber());

        autoSizeColumns(sheet, 4);
    }

}
