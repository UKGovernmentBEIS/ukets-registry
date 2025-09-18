package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentTableUtilsTest {

    private PdfFormatter pdfFormatter;
    private PaymentTableUtils paymentTableUtils;

    @BeforeEach
    void setUp() {
        pdfFormatter = mock(PdfFormatter.class);
        paymentTableUtils = new PaymentTableUtils(pdfFormatter);
    }

    @Test
    void testAddPaymentTable_addsAllExpectedCells() {
        PdfPCell mockHeader = new PdfPCell(new Phrase("Header"));
        PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        PdfPCell totalValueCell = new PdfPCell(new Phrase("£99.99"));

        when(pdfFormatter.boldCell(anyString())).thenReturn(mockHeader);
        when(pdfFormatter.emptyCell()).thenReturn(emptyCell);
        when(pdfFormatter.poundsBoldCell("£99.99")).thenReturn(totalValueCell);

        List<String[]> data = List.of(
                new String[]{"Item 1", "10", "2", "20"},
                new String[]{"Item 2", "5", "3", "15"}
        );
        PdfPTable table = paymentTableUtils.addPaymentTable(data, "£99.99");
        int totalExpectedCells = 4 /* headers */ + 8 /* 2 data rows */ + 4 /* total section */;
        int actualCellCount = table.getRows().stream()
                .mapToInt(row -> row.getCells().length)
                .sum();

        assertEquals(totalExpectedCells, actualCellCount);
    }

    @Test
    void testAddPaymentTable_correctColumnWidthsAndSettings() {
        when(pdfFormatter.boldCell(anyString())).thenReturn(new PdfPCell());
        when(pdfFormatter.emptyCell()).thenReturn(new PdfPCell());
        when(pdfFormatter.poundsBoldCell(anyString())).thenReturn(new PdfPCell());

        List<String[]> data = List.of();
        PdfPTable table = paymentTableUtils.addPaymentTable(data, "£0.00");
        assertEquals(4, table.getNumberOfColumns());
        assertEquals(100f, table.getWidthPercentage());
    }
}
