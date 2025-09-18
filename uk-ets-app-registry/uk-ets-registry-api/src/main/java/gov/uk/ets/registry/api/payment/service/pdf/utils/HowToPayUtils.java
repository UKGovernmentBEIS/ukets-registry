package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.Color;

@Component
@RequiredArgsConstructor
public class HowToPayUtils {

    private static final String HOW_TO_PAY = "How to Pay";
    private static final String CARD_METHOD = "Gov Pay (Debit/Credit Card, Apple/Google Pay)";
    private static final String BANK_METHOD = "Direct bank transfer via BACS, CHAPS or SWIFT";
    private static final String NOTE = "Note - using this method could delay your application";
    private static final String LINK_LABEL = "Please use this link: ";
    private static final String LINK_EMPTY_PLACE_HOLDER = "A secure payment link will be generated after submission";
    private static final String PAYMENT_INFO_MESSAGE = "Please quote 'ETG-UKETS/REG' followed by the account holder name " +
            "and full account number of your UK ETS Registry Account (if known) " +
            "so we can trace your payment.";

    private static final String ACCOUNT_NAME = "EA RECEIPTS";
    private static final String BANK_NAME = "NATWEST";
    private static final String SORT_CODE = "60-70-80";
    private static final String ACCOUNT_NUMBER = "10014411";
    private static final String IBAN_NUMBER = "GB23NWBK60708010014411";
    private static final String SWIFT_GIC_CODE = "NWBKGB2L";

    private final PdfFormatter formatter;

    public PdfPTable buildPaymentDetailsTable(String paymentLink) {

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);

        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(Color.BLACK);
        cell.setBorderWidth(1f);
        cell.setPadding(8f);
        cell.addElement(formatter.centeredBoldParagraph(HOW_TO_PAY));
        cell.addElement(formatter.normalParagraph(CARD_METHOD));
        if(paymentLink == null) {
            cell.addElement(formatter.labelWithBoldValue(LINK_LABEL, LINK_EMPTY_PLACE_HOLDER));
        } else {
            cell.addElement(formatter.labelWithBoldValue(LINK_LABEL, paymentLink));
        }
        cell.addElement(formatter.underLineParagraph(BANK_METHOD));
        cell.addElement(formatter.noteGrayParagraph(NOTE));
        addBankDetails(cell);
        table.addCell(cell);
        return table;
    }

    private void addBankDetails(PdfPCell cell) {
        cell.addElement(formatter.normalParagraph("Account Name: " + ACCOUNT_NAME));
        cell.addElement(formatter.normalParagraph("Bank Name: " + BANK_NAME));
        cell.addElement(formatter.normalParagraph("Sort Code: " + SORT_CODE));
        cell.addElement(formatter.normalParagraph("Account Number: " + ACCOUNT_NUMBER));
        cell.addElement(formatter.normalParagraph("IBAN Number: " + IBAN_NUMBER));
        cell.addElement(formatter.normalParagraph("SWIFT/BIC Code: " + SWIFT_GIC_CODE));
        cell.addElement(formatter.normalParagraph(PAYMENT_INFO_MESSAGE));
    }
}
