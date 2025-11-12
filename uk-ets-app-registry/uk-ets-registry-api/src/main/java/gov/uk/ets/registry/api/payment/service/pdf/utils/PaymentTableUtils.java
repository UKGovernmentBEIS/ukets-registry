package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.uk.ets.registry.api.payment.service.pdf.PaymentInvoicePdfUtils.COLUMN_1;
import static gov.uk.ets.registry.api.payment.service.pdf.PaymentInvoicePdfUtils.COLUMN_2;
import static gov.uk.ets.registry.api.payment.service.pdf.PaymentInvoicePdfUtils.COLUMN_3;
import static gov.uk.ets.registry.api.payment.service.pdf.PaymentInvoicePdfUtils.COLUMN_4;

@Component
@RequiredArgsConstructor
public class PaymentTableUtils {

    private static final List<String> TABLE_HEADERS =
            List.of(COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4);
    private static final Integer TABLE_CELL_PADDING = 5;

    private final PdfFormatter pdfFormatter;

    public PdfPTable addPaymentTable(List<String[]> dataRows,
                                     String total,
                                     String totalPaidLabel
    ) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{3f, 1f, 1f, 1f});
        addHeaders(table);
        addContent(table, dataRows);
        addTotal(table, total, totalPaidLabel);
        return table;
    }

    private void addTotal(PdfPTable table, String total, String totalPaidLabel) {
        PdfPCell emptyCell = pdfFormatter.emptyCell();
        emptyCell.setColspan(2);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyCell);

        PdfPCell labelCell = pdfFormatter.boldCell(totalPaidLabel);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(TABLE_CELL_PADDING);
        table.addCell(labelCell);

        PdfPCell valueCell = pdfFormatter.poundsBoldCell(total);
        valueCell.setPadding(TABLE_CELL_PADDING);
        table.addCell(valueCell);
    }

    private void addContent(PdfPTable table, List<String[]> dataRows) {
        for (String[] row : dataRows) {
            for (String cellText : row) {
                PdfPCell cell = new PdfPCell(new Phrase(cellText));
                cell.setPadding(TABLE_CELL_PADDING);
                table.addCell(cell);
            }
        }
    }


    private void addHeaders(PdfPTable table) {
        TABLE_HEADERS.forEach(cellDescription -> {
            PdfPCell header = pdfFormatter.boldCell(cellDescription);
            header.setPadding(TABLE_CELL_PADDING);
            table.addCell(header);
        });
    }
}
