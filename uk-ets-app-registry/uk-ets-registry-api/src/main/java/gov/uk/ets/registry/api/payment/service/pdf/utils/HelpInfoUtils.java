package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@RequiredArgsConstructor
public class HelpInfoUtils {

    private static final String TITLE = "Further Information";
    private static final String ADDITIONAL_INFO_TITLE = "Additional Information";
    private static final String THANK_YOU = "Thank you for your payment. This has been processed. If you have paid through " +
            "Gov Pay, you may also receive additional confirmation from them. ";
    private static final String CONTACT_US = "Contact us";
    private static final String HINT = "If you need any assistance with your application, " +
            "please contact the Registry Team by emailing etregistryhelp@environment-agency.gov.uk. " +
            "Please include the Request ID of your application in the subject line of your e-mail.";

    private static final String HINT_RECIPE = "If you need any assistance with your application, " +
            "please contact the Registry Team by emailing etregistryhelp@environment-agency.gov.uk. " +
            "Please include the Request ID of your application in the subject line of your e-mail.";

    private static final String LABEL = "Environment Agency Greenhouse Gas Charges";
    private static final String LINK_LABEL = "Environment Agency (greenhouse gas emissions) charging scheme - GOV.UK";
    private static final String LINK = "https://www.gov.uk/government/publications/environment-agency-greenhouse-gas-emissions-charging-scheme-2021";

    private final PdfFormatter formater;

    public PdfPTable addHelpInfoDetails() {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);

        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(Color.BLACK);
        cell.setBorderWidth(1f);
        cell.setPadding(8f);
        cell.addElement(formater.centeredBoldParagraph(TITLE));
        cell.addElement(formater.underLineParagraph(CONTACT_US));
        cell.addElement(formater.normalParagraph(HINT));
        cell.addElement(formater.hyperLink(LINK_LABEL, LINK));
        table.addCell(cell);
        return table;
    }

    public PdfPTable addAdditionalInfoRecipe() {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);

        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(Color.BLACK);
        cell.setBorderWidth(1f);
        cell.setPadding(8f);
        cell.addElement(formater.centeredBoldParagraph(ADDITIONAL_INFO_TITLE));
        cell.addElement(formater.normalParagraph(THANK_YOU));
        cell.addElement(Chunk.NEWLINE);
        cell.addElement(Chunk.NEWLINE);
        cell.addElement(Chunk.NEWLINE);
        cell.addElement(formater.underLineParagraph(CONTACT_US));
        cell.addElement(formater.normalParagraph(HINT_RECIPE));
        table.addCell(cell);
        return table;
    }
}
