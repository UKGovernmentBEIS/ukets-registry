package gov.uk.ets.registry.api.payment.service.pdf;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdfFormatterTest {

    private PdfFormatter pdfFormatter;

    @BeforeEach
    void setUp() {
        pdfFormatter = new PdfFormatter();
    }

    @Test
    void boldCell_shouldReturnBoldPdfPCell() {
        PdfPCell cell = pdfFormatter.boldCell("Bold Text");

        assertEquals(PdfFormatter.CELL_PADDING, cell.getPaddingBottom());
        assertTrue(((Phrase) cell.getPhrase()).getContent().contains("Bold Text"));
    }

    @Test
    void poundsBoldCell_shouldReturnCellWithCurrency() {
        PdfPCell cell = pdfFormatter.poundsBoldCell(100);

        assertEquals("100 £", cell.getPhrase().getContent());
        assertEquals(PdfFormatter.CELL_PADDING, cell.getPaddingBottom());
    }

    @Test
    void centeredBoldParagraph_shouldBeCentered() {
        Paragraph para = pdfFormatter.centeredBoldParagraph("Centered Text");
        assertEquals(Element.ALIGN_CENTER, para.getAlignment());
    }

    @Test
    void underLineParagraph_shouldHaveUnderlineFont() {
        Paragraph para = pdfFormatter.underLineParagraph("Underlined");
        assertEquals(Font.UNDERLINE, para.getFont().getStyle());
    }

    @Test
    void noteGrayParagraph_shouldBeGrayAndItalic() {
        Paragraph para = pdfFormatter.noteGrayParagraph("Note");
        Font font = para.getFont();
        assertEquals(Color.GRAY, font.getColor());
    }

    @Test
    void emptyCell_shouldBeEmpty() {
        PdfPCell cell = pdfFormatter.emptyCell();
        assertEquals("", cell.getPhrase().getContent().trim());
    }

    @Test
    void rightHeader_shouldHandleNullSubTaskId() {
        PdfPCell cell = pdfFormatter.rightHeader(null);
        assertTrue(cell.getCompositeElements().stream()
                .anyMatch(el -> ((Paragraph) el).getContent().contains(PaymentInvoicePdfUtils.NA)));
    }

    @Test
    void title_shouldReturnValidTitleCell() throws IOException {
        PdfPCell titleCell = pdfFormatter.title(true);

        assertEquals(Rectangle.NO_BORDER, titleCell.getBorder());
        assertEquals(Element.ALIGN_CENTER, titleCell.getHorizontalAlignment());
    }

    @Test
    void logo_shouldLoadAndReturnCell() throws IOException {
        PdfPCell logoCell = pdfFormatter.logo();

        assertEquals(Rectangle.NO_BORDER, logoCell.getBorder());
        assertEquals(Element.ALIGN_MIDDLE, logoCell.getVerticalAlignment());
    }

    @Test
    void normalParagraph_shouldHaveCorrectFont() {
        Paragraph para = pdfFormatter.normalParagraph("Normal Text");

        assertEquals(FontFactory.HELVETICA, para.getFont().getFamilyname());
        assertEquals(12, para.getFont().getSize(), 0.1);
    }
}
