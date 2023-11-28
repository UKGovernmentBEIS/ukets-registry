package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi;

import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl.RREG3NotificationItem;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r4itl.UnitConcerned;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.util.ApplicationLabels;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * The RITL4ExcelHelper is the class where
 * the excel file is created (cells, sheets, titles etc.)
 *
 * @author samarasn
 *
 */
public class RREG4ExcelHelper extends RREGExcelHelper {

    public RREG4ExcelHelper() {
        super();
    }

    /**
     * Notification List Headers
     */
    public void addNotificationListHeaders() {
        Sheet sheet = getWorkbook().getSheetAt(0);
        createMainNotificationListHeaderRow(sheet, 1, ApplicationLabels.get("label.rreg4NotificationType"),
                ApplicationLabels.get("label.rreg4NotificationNumber"));
        createSubNotificationListHeaderRow(sheet);
    }

    /**
     * Notification Item headers
     */
    public void addNotificationHeaders() {
        Sheet sheet = getWorkbook().getSheetAt(0);
        createMainNotificationListHeaderRow(sheet, 3, ApplicationLabels.get("label.rreg4NotificationNumber"),
                ApplicationLabels.get("label.rreg4NotificationType"));
        createSubNotificationListHeaderRow(sheet);
    }

    /**
     * Units concerned headers
     */
    public void addUnitsHeaders() {
        Sheet sheet = getWorkbook().getSheetAt(0);
        createUnitsHeaderRow(sheet);
        createSubUnitHeaderRow(sheet);
    }

    private void createMainNotificationListHeaderRow(Sheet sheet, int rowStart, String firstColumnTitle, String secondColumnTitle){
        Row headerRow = sheet.createRow(sheet.getLastRowNum() + rowStart);
        headerRow.setHeightInPoints((5 * sheet.getDefaultRowHeightInPoints()));

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue(firstColumnTitle);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum() + 1, 0, 0));

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue(secondColumnTitle);
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum() + 1, 1, 1));

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg4NotificationNumberDateTime"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum() + 1, 2, 2));

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg4TargetNumberUnits"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum() + 1, 3, 3));

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg4Cancelation"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum() + 1, 4, 4));

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg4Replacement"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum() + 1, 5, 5));

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg4NumUnitsDifferenceTargetAndCancellationReplacements"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 6, 7));

        headerCell = headerRow.createCell(8);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg4Explanation"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum() + 1, 8, 8));
    }

    private void createSubNotificationListHeaderRow(Sheet sheet) {
        Row subHeaderRow = sheet.createRow(sheet.getLastRowNum() + 1);
        subHeaderRow.setHeightInPoints((4 * sheet.getDefaultRowHeightInPoints()));

        Cell subHeaderCell = subHeaderRow.createCell(6);
        subHeaderCell.setCellValue(ApplicationLabels.get("label.rreg4AtTargetDate"));
        subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));

        subHeaderCell = subHeaderRow.createCell(7);
        subHeaderCell.setCellValue(ApplicationLabels.get("label.rreg4PostTargetDate"));

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

    private void createUnitsHeaderRow(Sheet sheet){
        Row headerRow = sheet.createRow(sheet.getLastRowNum() + 2);
        headerRow.setHeightInPoints((2 * sheet.getDefaultRowHeightInPoints()));

        Cell headerCell = headerRow.createCell(6);
        headerCell.setCellValue(ApplicationLabels.get("label.rreg4UnitsConcerned"));
        headerCell.setCellStyle(getHeaderStyle(getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 6, 8));

    }

    private void createSubUnitHeaderRow(Sheet sheet) {
        Row subHeaderRow = sheet.createRow(sheet.getLastRowNum() + 1);

        Cell subHeaderCell = subHeaderRow.createCell(6);
        subHeaderCell.setCellValue(ApplicationLabels.get("label.rreg4SerialNumber"));
        subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));

        subHeaderCell = subHeaderRow.createCell(7);
        subHeaderCell.setCellValue(ApplicationLabels.get("label.rreg4UnitType"));
        subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));

        subHeaderCell = subHeaderRow.createCell(8);
        subHeaderCell.setCellValue(ApplicationLabels.get("label.rreg4Quantity"));
        subHeaderCell.setCellStyle(getHeaderStyle(getWorkbook()));
    }

    public void addNotificationTypeDescription(String notificationTypeCodeDescr) {
        Sheet sheet = getWorkbook().getSheetAt(0);
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        Cell headerCell = row.createCell(0);

        headerCell.setCellStyle(getNormalStyle(getWorkbook()));
        headerCell.setCellValue(notificationTypeCodeDescr);
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
        addCellNotificationItem(item, sheet, sheet.getLastRowNum(), countColumn, false, null);

        autoSizeColumns(sheet, 9);

    }

    /**
     * Fill values in the cells according to the Notification Type list result.
     * We call this method from the addNotificationList when we get the list
     * of {@link RREG3NotificationItem}
     *
     */
    private void addCellNotificationItem(RREG3NotificationItem item, Sheet sheet, int countRow, int countColumn,
                                         boolean showNotificationType, String notificationType) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);

        // Notification Number
        Cell cell = row.createCell(countColumn);
        cell.setCellStyle(getNormalStyle(getWorkbook()));
        cell.setCellValue(Long.parseLong(item.getNotificationIdentifier()));
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);


        if (showNotificationType) {
            countColumn++;
            cell = row.createCell(countColumn);
            cell.setCellStyle(getNormalStyle(getWorkbook()));
            cell.setCellValue(notificationType);
        }

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

    public void addNotification(RREG3NotificationItem item, String notificationType) {

        Sheet sheet = getWorkbook().getSheetAt(0);

        int countColumn = 0; // Starting from the 1st column in the Excel

        // Fill the cells according to the item RITL3NotificationItem
        addCellNotificationItem(item, sheet, sheet.getLastRowNum()+1, countColumn,
                true, notificationType);

        autoSizeColumns(sheet, 9);
    }

    public void addCellUnitItem(UnitConcerned item) {
        Sheet sheet = getWorkbook().getSheetAt(0);
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);

        // Notification Number
        Cell cell = row.createCell(6);
        cell.setCellStyle(getNormalStyle(getWorkbook()));
        cell.setCellValue(item.getSerialNumber());

        cell = row.createCell(7);
        cell.setCellStyle(getNormalStyle(getWorkbook()));
        cell.setCellValue(item.getUnitType());

        cell = row.createCell(8);
        cell.setCellStyle(getNormalStyle(getWorkbook()));
        cell.setCellValue(item.getQuantity());
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);

        autoSizeColumns(sheet, 9);
    }
}
