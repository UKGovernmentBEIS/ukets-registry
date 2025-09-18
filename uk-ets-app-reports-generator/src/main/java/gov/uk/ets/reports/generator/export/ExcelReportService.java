package gov.uk.ets.reports.generator.export;

import static gov.uk.ets.reports.generator.export.util.ComplianceReportTypeHelper.*;

import gov.uk.ets.reports.generator.ReportGeneratorException;
import gov.uk.ets.reports.generator.domain.ReportData;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.dhatim.fastexcel.Color;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private final Map<ReportType, ReportTypeService<ReportData>> reportTypeServiceMap;
    public static final int DEFAULT_FONT_SIZE = 11;
    private static final String EMPTY_CELL_DEFAULT_VALUE = null;

    /**
     * Write report.
     *
     * @param command the report request command.
     */
    public final byte[] writeReport(ReportGenerationCommand command) {
        ReportTypeService<ReportData> reportTypeService = reportTypeServiceMap.get(command.getType());
        if (reportTypeService == null) {
            throw new ReportGeneratorException(String.format("Not supported report type for :%s", command.getType()));
        }
        // TODO possibly  keyset pagination in select
        List<ReportData> reportData = reportTypeService.generateReportData(command.getReportQueryInfo());
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Workbook wb = new Workbook(os, "MyApplication", "1.0");
            Worksheet ws = wb.newWorksheet("Data");
            List<String> reportHeaders = reportTypeService.getReportHeaders(command.getReportQueryInfo().getYear());
            final var workSheetCols = reportHeaders.size();
            final var workSheetRows = reportData.size();
            final var lastColumnIndex = workSheetCols == 0 ? workSheetCols : workSheetCols - 1;
            final var lastRowIndex = workSheetRows == 0 ? workSheetRows : workSheetRows - 1;
            writeHeaders(ws, reportHeaders);
            List<Integer> emissionComplianceColumns = reportTypeChoice(command, reportHeaders);
            //insert data rows
            for (int row = 0; row < workSheetRows; row++) {
                List<Object> reportDataRow = reportTypeService.getReportDataRow(reportData.get(row));
                if (row == 0 && reportDataRow.size() != workSheetCols) {
                    throw new ReportGeneratorException("Row and header data do not match");
                }
                for (int column = 0; column < reportDataRow.size(); column++) {
                    writeValue(ws, reportDataRow.get(column), row + 1, column, emissionComplianceColumns);
                    setColor(reportTypeService, ws, row + 1, column, reportDataRow.get(column));
                }
            }
            //set Name to Header column
            ws.range(0, 0, 0, lastColumnIndex).setName("Header");
            //set header to auto filter
            ws.setAutoFilter(0, 0, lastColumnIndex);
            //set font to entire worksheet
            ws.range(0, 0, lastRowIndex, lastColumnIndex).style().fontSize(DEFAULT_FONT_SIZE).set();
            //header style
            ws.range(0, 0, 0, lastColumnIndex).style().bold()
                .fontSize(DEFAULT_FONT_SIZE)
                .fillColor(Color.GRAY9)
                .fontColor(Color.WHITE)
                .set();
            wb.finish();
            return os.toByteArray();
        } catch (IOException e) {
            throw new ReportGeneratorException("Exception occurred while reading/writing the file", e);
        }
    }

    private void writeHeaders(Worksheet ws, List<String> reportHeaders) {
        for (int headerColumn = 0; headerColumn < reportHeaders.size(); headerColumn++) {
            ws.value(0, headerColumn, reportHeaders.get(headerColumn));
        }
    }

    private void writeValue(Worksheet ws, Object value, int row, int column, List<Integer> columnIndexes) {
        if (value == null) {
            ws.value(row, column, EMPTY_CELL_DEFAULT_VALUE);
        } else if (value instanceof String) {
            if (CollectionUtils.isNotEmpty(columnIndexes) &&
                    columnIndexes.contains(column) &&
                    isNumeric((String) value)){
                ws.value(row, column, convertToNumber(value));
            } else {
                ws.value(row, column, (String) value);
            }
        } else if (value instanceof Boolean) {
            ws.value(row, column, (Boolean) value);
        } else if (value instanceof Number) {
            ws.value(row, column, (Number) value);
        } else if (value instanceof Date) {
            ws.value(row, column, (Date) value);
        } else if (value instanceof LocalDate) {
            ws.value(row, column, (LocalDate) value);
            ws.style(row, column).format("d MMMM yyyy").set();
        } else if (value instanceof LocalDateTime) {
            ws.value(row, column, (LocalDateTime) value);
            ws.style(row, column).format("yyyy-MM-dd H:mm:ss").set();
        } else {
            throw new ReportGeneratorException(String.format("data type not supported :%s", value.getClass()));
        }
    }

    private void setColor(ReportTypeService<ReportData> reportTypeService, Worksheet ws, int row, int column, Object value) {
        String cellColor = reportTypeService.getCellColor(column, value);
        if (cellColor != null) {
            ws.style(row, column).fillColor(cellColor).set();
        }
    }
}

