package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelpInfoUtilsTest {

    private PdfFormatter mockFormatter;
    private HelpInfoUtils helpInfoUtils;

    @BeforeEach
    void setUp() {
        mockFormatter = mock(PdfFormatter.class);
        helpInfoUtils = new HelpInfoUtils(mockFormatter);

        when(mockFormatter.centeredBoldParagraph("Further Information")).thenReturn(new Paragraph("Centered Bold"));
        when(mockFormatter.underLineParagraph("Contact us")).thenReturn(new Paragraph("Underlined Contact Us"));
        when(mockFormatter.normalParagraph(Mockito.startsWith("If you need any assistance"))).thenReturn(new Paragraph("Hint Paragraph"));
        when(mockFormatter.hyperLink(
                "Environment Agency (greenhouse gas emissions) charging scheme - GOV.UK",
                "https://www.gov.uk/government/publications/environment-agency-greenhouse-gas-emissions-charging-scheme-2021"
        )).thenReturn(new Paragraph("Hyperlink"));
    }

    @Test
    void testAddHelpInfoDetails_returnsCorrectTableStructure() {
        PdfPTable table = helpInfoUtils.addHelpInfoDetails();

        assertNotNull(table);
        assertEquals(1, table.getNumberOfColumns());
        assertEquals(1, table.size()); // only 1 cell expected

        PdfPCell cell = table.getRow(0).getCells()[0];
        assertNotNull(cell);
        assertEquals(4, cell.getCompositeElements().size());

        // Optional: Verify order of elements if needed
        assertTrue(cell.getCompositeElements().get(0) instanceof Paragraph);
    }

    @Test
    void testAddHelpInfoDetails_callsFormatterMethods() {
        helpInfoUtils.addHelpInfoDetails();

        verify(mockFormatter).centeredBoldParagraph("Further Information");
        verify(mockFormatter).underLineParagraph("Contact us");
        verify(mockFormatter).normalParagraph(Mockito.anyString());
        verify(mockFormatter).hyperLink(
                "Environment Agency (greenhouse gas emissions) charging scheme - GOV.UK",
                "https://www.gov.uk/government/publications/environment-agency-greenhouse-gas-emissions-charging-scheme-2021"
        );
    }
}
