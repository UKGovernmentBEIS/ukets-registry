package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.poi;

import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps the styles per workbook.
 *
 * @author gkountak
 */
public class StyleInfo {

    public final static String HEADER_CELL_STYLE = "header";
    public final static String HEADER_CELL_STYLE_LEFT = "headerLeft";
    public final static String NORMAL_CELL_STYLE = "normal";
    public final static String NORMAL_CELL_STYLE_2DECIMAL = "normal2decimal";
    public final static String BOTTOM_BORDER_CELL_STYLE = "bottomBorder";
    public final static String NORMAL_CELL_STYLE_2DECIMAL_PERCENTAGE = "normal2decimalpercentage";
    public final static String NORMAL_CELL_STYLE_PERCENTAGE = "normalpercentage";
    public final static String NORMAL_CELL_STYLE_COMMA_SEPERATED = "normalcommaseperated";
    public final static String NORMAL_CELL_NUMBER_AMERICAN_STYLE = "normalNumberAmericanStyle";

    /**
     * The workbook.
     */
    private Workbook workbook = null;

    private Map<String, CellStyle> cellStyles = null;

    private DataFormat dataFormat = null;

    /**
     * @param workbook
     */
    public StyleInfo(Workbook workbook) {
        super();
        this.workbook = workbook;
        dataFormat = workbook.createDataFormat();
    }

    /**
     * Create cell styles.
     */
    private void createCellStyles() {
        createNormalStyle(workbook);
        createHeaderStyle(workbook);
        createNumberStyles(workbook);
        createBorderStyle(workbook);
    }

    private void createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // normal style
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setWrapText(true);
        cellStyles.put(NORMAL_CELL_STYLE, style);
    }

    private void createHeaderStyle(Workbook workbook) {
        // header style
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyles.put(HEADER_CELL_STYLE, style);

        // header style left
        style = workbook.createCellStyle();
        font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyles.put(HEADER_CELL_STYLE_LEFT, style);
    }

    private void createBorderStyle(Workbook workbook) {
        // bottom border
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setWrapText(true);
        cellStyles.put(BOTTOM_BORDER_CELL_STYLE, style);
    }

    private void createNumberStyles(Workbook workbook) {
        // number with 2 decimal digits
        CellStyle style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setWrapText(true);
        style.setDataFormat(this.dataFormat.getFormat("#,##0.00"));
        cellStyles.put(NORMAL_CELL_STYLE_2DECIMAL, style);

        // number with 2 decimal digits
        style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setWrapText(true);
        style.setDataFormat(this.dataFormat.getFormat("#,##0"));
        cellStyles.put(NORMAL_CELL_STYLE_COMMA_SEPERATED, style);

        createCommaSeparatedNumberStyle(workbook);
        createPercentageNumberStyles(workbook);
    }

    private void createCommaSeparatedNumberStyle(Workbook workbook) {
        // number with 2 decimal digits
        CellStyle style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setWrapText(true);
        style.setDataFormat(this.dataFormat.getFormat("#,###,###"));
        cellStyles.put(NORMAL_CELL_NUMBER_AMERICAN_STYLE, style);
    }

    private void createPercentageNumberStyles(Workbook workbook){
        // number percentage with NO decimal digits
        CellStyle style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setWrapText(true);
        style.setDataFormat(this.dataFormat.getFormat("0%"));
        cellStyles.put(NORMAL_CELL_STYLE_PERCENTAGE, style);


        // number percentage with 2 decimal digits
        style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setWrapText(true);
        style.setDataFormat(this.dataFormat.getFormat("0.00%"));
        cellStyles.put(NORMAL_CELL_STYLE_2DECIMAL_PERCENTAGE, style);
    }

    /**
     * The header style
     *
     * @return
     */
    public CellStyle getHeaderCellStyle() {

        if (cellStyles == null || cellStyles.get(HEADER_CELL_STYLE) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(HEADER_CELL_STYLE);
    }

    /**
     * The header left style
     *
     * @return
     */
    public CellStyle getHeaderCellLeftStyle() {

        if (cellStyles == null || cellStyles.get(HEADER_CELL_STYLE_LEFT) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(HEADER_CELL_STYLE_LEFT);
    }

    /**
     * The normal cell style.
     *
     * @return
     */
    public CellStyle getNormalCellStyle() {

        if (cellStyles == null || cellStyles.get(NORMAL_CELL_STYLE) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(NORMAL_CELL_STYLE);
    }

    /**
     * The normal cell style.
     *
     * @return
     */
    public CellStyle getNormalCellStyle2Decimal() {

        if (cellStyles == null || cellStyles.get(NORMAL_CELL_STYLE_2DECIMAL) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(NORMAL_CELL_STYLE_2DECIMAL);
    }

    /**
     * The normal cell style with 2 decimal percentage.
     *
     * @return
     */
    public CellStyle getNormalCellStyle2DecimalPercentage() {

        if (cellStyles == null || cellStyles.get(NORMAL_CELL_STYLE_2DECIMAL_PERCENTAGE) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(NORMAL_CELL_STYLE_2DECIMAL_PERCENTAGE);
    }

    /**
     * The normal cell style percentage with NO decimal .
     *
     * @return
     */
    public CellStyle getNormalCellStylePercentage() {

        if (cellStyles == null || cellStyles.get(NORMAL_CELL_STYLE_PERCENTAGE) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(NORMAL_CELL_STYLE_PERCENTAGE);
    }


    /**
     * The normal cell style.
     *
     * @return
     */
    public CellStyle getBottomBorderCellStyle() {

        if (cellStyles == null || cellStyles.get(BOTTOM_BORDER_CELL_STYLE) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(BOTTOM_BORDER_CELL_STYLE);
    }

    /**
     * Comma seperated for triplet of digits
     * @return
     */
    public CellStyle getNormalCommaSeperatedCellStyle() {
        if (cellStyles == null || cellStyles.get(NORMAL_CELL_STYLE_COMMA_SEPERATED) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(NORMAL_CELL_STYLE_COMMA_SEPERATED);
    }

    /**
     * Number with American style (example: 110.563)
     * @return
     */
    public CellStyle getNumberAmericanCellStyle() {
        if (cellStyles == null || cellStyles.get(NORMAL_CELL_NUMBER_AMERICAN_STYLE) == null) {
            cellStyles = new HashMap<String, CellStyle>();
            createCellStyles();
        }

        return cellStyles.get(NORMAL_CELL_NUMBER_AMERICAN_STYLE);
    }
}
