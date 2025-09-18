package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HowToPayUtilsTest {

    private HowToPayUtils howToPayUtils;

    @BeforeEach
    void setUp() {
        PdfFormatter formatter = new PdfFormatter();
        howToPayUtils = new HowToPayUtils(formatter);
    }

    @Test
    void buildPaymentDetailsTable_shouldBuildCorrectTable() {
        String paymentLink = "paymentLink";
        PdfPTable table = howToPayUtils
                .buildPaymentDetailsTable(paymentLink);

        assertEquals(1, table.getNumberOfColumns());
        assertEquals(1, table.getRows().size());

        PdfPCell cell = table.getRow(0).getCells()[0];
        List<Element> elements = cell.getCompositeElements();
        assertEquals(12, elements.size(), "Expected 12 elements: 6 sections + 6 bank details");

        assertTrue(elements.get(0).toString().contains("How to Pay"));
        assertTrue(elements.stream().anyMatch(el -> el.toString().contains("Account Name: EA RECEIPTS")));
    }
}
