package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 *
 * @author gkountak
 */
@Log4j2
public class RREGExcelHelper {

    /**
     * The workbook instance to work with.
     */
    private Workbook workbook;
    private StyleInfo si;

    public static final String XLSX_SUFFIX = ".xlsx";

    /**
     * Initializes the workbook and workbook styles instances
     */
    public RREGExcelHelper() {
        workbook = new XSSFWorkbook();
        si = new StyleInfo(workbook);
    }

    /**
     * @return the workbook
     */
    public Workbook getWorkbook() {
        return workbook;
    }

    /**
     * @param workbook
     *            the workbook to set
     */
    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    /**
     * @return the si
     */
    public StyleInfo getStyleInfo() {
        return si;
    }

    /**
     * @param si
     *            the si to set
     */
    public void setStyleInfo(StyleInfo si) {
        this.si = si;
    }

    /**
     * Saves the workbook to the file in the parameter.
     *
     * @return
     * @throws IOException
     */
    public byte [] write() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Add a title.
     *
     * @param reportTitle
     * @param registry
     * @param reportedYear
     * @param reportingYear
     * @return
     */
    public void addTitle(String reportTitle, String registry, short reportedYear, short reportingYear) {
        Sheet sheet = workbook.createSheet(reportTitle);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        Row aRow = sheet.createRow(1);
        Cell titleCell = aRow.createCell(0);
        titleCell.setCellValue("Report Type: " + reportTitle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

        aRow = sheet.createRow(2);
        titleCell = aRow.createCell(0);
        titleCell.setCellValue("Registry: " + registry);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));

        aRow = sheet.createRow(3);
        titleCell = aRow.createCell(0);
        titleCell.setCellValue("Reported Year: " + reportedYear);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 9));

        aRow = sheet.createRow(4);
        titleCell = aRow.createCell(0);
        titleCell.setCellValue(reportingYear != 0 ? "Reporting Year: " + reportingYear : " ");
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 9));

        aRow = sheet.createRow(5);
        titleCell = aRow.createCell(0);
        titleCell.setCellValue(" ");
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 9));
    }

    public void addTitle(String reportTitle, String registry, short reportedYear) {
        addTitle(reportTitle, registry, reportedYear, (short) 0);
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getNormalStyle(Workbook workbook) {
        return si.getNormalCellStyle();
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getNormalCommaSeperatedStyle(Workbook workbook) {
        return si.getNormalCommaSeperatedCellStyle();
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getBottomBorderStyle(Workbook workbook) {
        return si.getBottomBorderCellStyle();
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getNormal2DecimalStyle(Workbook workbook) {
        return si.getNormalCellStyle2Decimal();
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getNormal2DecimalPercentageStyle(Workbook workbook) {
        return si.getNormalCellStyle2DecimalPercentage();
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getNormalPercentageStyle(Workbook workbook) {
        return si.getNormalCellStylePercentage();
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getHeaderStyle(Workbook workbook) {
        return si.getHeaderCellStyle();
    }

    /**
     *
     * @param workbook
     * @return Number in American style (example 110.563)
     */
    protected CellStyle getNumberAmericanCellStyle(Workbook workbook) {
        return si.getNumberAmericanCellStyle();
    }

    /**
     *
     * @param workbook
     * @return
     */
    protected CellStyle getHeaderCellLeftStyle(Workbook workbook) {
        return si.getHeaderCellLeftStyle();
    }

    /**
     * Resizing of columns at the end of the process. All the values are in the
     * cells when the process starts
     *
     * @param sheet
     */
    public void autoSizeColumns(Sheet sheet, int numOfColumns) {
        for (int col = 0; col < numOfColumns; col++) {
            sheet.autoSizeColumn(col, true);
        }
    }

    public String getHalfEvenRound(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(value);
    }

    public String getHalfEvenRoundWith4Digits(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.0000");
        return df.format(value);
    }

    private static boolean isNumeric(String str) {
        try {
            String value = str;
            if (containsComma(value)) {

                NumberFormat nf = NumberFormat.getInstance();
                nf.parse(value).doubleValue();

                return true;

            } else {
                Double.parseDouble(str);
                return true;
            }
        } catch (NumberFormatException | ParseException nfe) {
            return false;
        }
    }

    /**
     *  Retrieves the cell type
     * @param cell the cell
     * @param value the cell's value
     * @return the cell instance
     */
    public static Cell getCellTypeDouble(Cell cell, String value) {

        if (isNumeric(value)) {
            try {
                if (containsComma(value)) {
                    NumberFormat nf = NumberFormat.getInstance();
                    cell.setCellValue(nf.parse(value).doubleValue());
                } else {
                    cell.setCellValue(Double.parseDouble(value));
                }
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            } catch (ParseException e) {
                log.error("Error getCellType : " + e.toString());
            }
        } else {
            cell.setCellValue(value);
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        return cell;
    }

    /**
     *
     * @param cell
     * @param value
     * @param workbook
     * @return
     */
    public Cell getCellTypeLong(Cell cell, String value, Workbook workbook) {

        if (!value.equals("0")) {
            cell.setCellValue(Long.parseLong(value));
            cell.setCellStyle(getNumberAmericanCellStyle(workbook));
        } else {
            cell.setCellValue(Long.parseLong(value));
            cell.setCellStyle(getNormalStyle(workbook));
        }

        cell.setCellType(Cell.CELL_TYPE_NUMERIC);

        return cell;
    }

    /**
     *
     * @param str
     * @return
     */
    private static boolean containsComma(String str) {
        return str.contains(",");
    }

}
