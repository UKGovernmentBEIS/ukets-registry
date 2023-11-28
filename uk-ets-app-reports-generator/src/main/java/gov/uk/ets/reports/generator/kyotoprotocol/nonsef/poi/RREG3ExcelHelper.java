package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi;

import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl.RREG3NotificationItem;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.util.ApplicationLabels;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * The RITL3ExcelHelper is the class where the excel file is created (cells,
 * sheets, titles etc.)
 *
 * @author samarasn
 */
public class RREG3ExcelHelper extends RREGExcelHelper {

    /**
     *
     */
    public RREG3ExcelHelper() {
        super();
    }

    /**
     * Add the headers.
     */
    public void addHeaders() {

        Sheet sheet = getWorkbook().getSheetAt(0);
        createMainHeaderRow(sheet);
        createSubHeaderRow(sheet);
    }

    /**
     * Creates the sheet's main header row
     * @param sheet {@link Sheet}
     */
    private void createMainHeaderRow(Sheet sheet){
        Row headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
        headerRow.setHeightInPoints((10 * sheet.getDefaultRowHeightInPoints()));

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3NotificationType"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 0, 0));

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3NotificationNumber"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 1, 1));

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3NotificationNumberDateTime"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 2, 2));

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3TargetNumberUnits"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 3, 3));

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3Cancelation"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 4, 4));

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3Replacement"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 5, 5));

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3NumUnitsDifferenceTargetAndCancellationReplacements"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 6, 7));

        headerCell = headerRow.createCell(8);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg3Explanation"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 8, 8));
    }

    /**
     * Create the sheet's sub-headers
     *
     * @param sheet {@link Sheet}
     */
    private void createSubHeaderRow(Sheet sheet) {
        Row subHeaderRow = sheet.createRow(sheet.getLastRowNum() + 1);
        subHeaderRow.setHeightInPoints((4 * sheet.getDefaultRowHeightInPoints()));

        Cell subHeaderCell = subHeaderRow.createCell(6);
        subHeaderCell.setCellValue(ApplicationLabels.get("label.rreg3AtTargetDate"));
        subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));

        subHeaderCell = subHeaderRow.createCell(7);
        subHeaderCell.setCellValue(ApplicationLabels.get("label.rreg3PostTargetDate"));
        subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));

        /*
         * Empty cells Under the headers which don't have subHeader
         */
        for (int i = 0; i < 6; i++) {
            subHeaderCell = subHeaderRow.createCell(i);
            subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));
        }

        subHeaderCell = subHeaderRow.createCell(8);
        subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));
    }

    /**
     * Adds a notification type description in the cell
     * @param notificationTypeCodeDescr the notification text to insert
     */
    public void addNotificationTypeDescr(String notificationTypeCodeDescr) {
        Sheet sheet = getWorkbook().getSheetAt(0);
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        Cell headerCell = row.createCell(0);

        // Calling the function that completes the title <Notification Type> for
        // each new notificationType.
        ritl3NotifcationType(notificationTypeCodeDescr, sheet, headerCell, sheet.getLastRowNum() + 1);
    }

    /**
     * This function get the list of the RITL3NotificationItem and calls the
     * addCellNotificationItem method which fills the values in the cells
     *
     */
    public void addNotificationList(RREG3NotificationItem item) {

        Sheet sheet = getWorkbook().getSheetAt(0);

        int countColumn = 1; // Starting from the 1st column in the Excel

        // Fill the cells according to the item RITL3NotificationItem
        addCellNotificationItem(item, sheet, sheet.getLastRowNum(), countColumn);

        autoSizeColumns(sheet, 9);

    }

    /**
     * Fill values in the cells according to the Notification Type list result.
     * We call this method from the the addNotificationList when we get the list
     * of RITL3NotificationItem
     *
     */
    private void addCellNotificationItem(RREG3NotificationItem item, Sheet sheet, int countRow, int countColumn) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);

        // Notification Number
        Cell cell = row.createCell(countColumn);
        cell.setCellStyle(getNormalStyle(getWorkbook()));
        cell.setCellValue(Long.parseLong(item.getNotificationIdentifier()));
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);

        // Notification Date
        cell = row.createCell(countColumn + 1);
        cell.setCellStyle(getNormalStyle(getWorkbook()));
        cell.setCellValue(item.getRegistryNotificationDate());

        // Target Number of Units
        cell = row.createCell(countColumn + 2);
        cell = getCellTypeLong(cell, item.getTargetValue(), getWorkbook());

        // Cancelation
        cell = row.createCell(countColumn + 3);
        String cellValue = item.getCancelation() == null ? "0" : item.getCancelation();
        cell = getCellTypeLong(cell, cellValue, getWorkbook());

        // Replacement
        cell = row.createCell(countColumn + 4);
        cell.setCellStyle(getNumberAmericanCellStyle(getWorkbook()));
        cellValue = item.getReplacement() == null ? "0" : item.getReplacement();
        cell = getCellTypeLong(cell, cellValue, getWorkbook());

        // At Target Date
        cell = row.createCell(countColumn + 5);
        cell.setCellStyle(getNumberAmericanCellStyle(getWorkbook()));
        cellValue = item.getTargetDate() == null ? "0" : item.getTargetDate();
        cell = getCellTypeLong(cell, cellValue, getWorkbook());

        // Post Target Date
        cell = row.createCell(countColumn + 6);
        cell.setCellStyle(getNumberAmericanCellStyle(getWorkbook()));
        cellValue = item.getPostTargetDate() == null ? "0" : item.getPostTargetDate();
        cell = getCellTypeLong(cell, cellValue, getWorkbook());
    }

    /**
     * For each new Notification Type group the <Notification Type> title is
     * completed
     *
     */
    private void ritl3NotifcationType(String notTypeDescr, Sheet sheet, Cell cell, int rowNum) {
        cell.setCellStyle(getNormalStyle(getWorkbook()));
        cell.setCellValue(notTypeDescr);
    }

}
