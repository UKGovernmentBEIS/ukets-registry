package gov.uk.ets.registry.api.payment.service.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;

import static gov.uk.ets.registry.api.payment.service.pdf.PaymentInvoicePdfUtils.*;

@Component
public class PdfFormatter {
    private static final int FONT_SIZE = 12;
    public static final float CELL_PADDING = 1f;
    private static final String CURRENCY = " £";

    public Paragraph labelWithBoldValue(String label, String value) {
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE)));
        phrase.add(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE)));
        return prepareParagraph(new Paragraph(phrase));
    }

    public PdfPCell boldCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        return prepareCell(cell);
    }

    public PdfPCell poundsBoldCell(Object value) {
        PdfPCell cell = new PdfPCell(new Phrase(value + CURRENCY, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        return prepareCell(cell);
    }

    public Paragraph centeredBoldParagraph(String text) {
        Paragraph para = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE));
        para.setAlignment(Element.ALIGN_CENTER);
        return prepareParagraph(para);
    }

    public Paragraph boldParagraph(String text) {
        return prepareParagraph(new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE)));
    }

    public Paragraph underLineParagraph(String text) {
        return prepareParagraph(new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE, Font.UNDERLINE)));
    }

    public Paragraph noteGrayParagraph(String text) {
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);
        return prepareParagraph(new Paragraph(text, noteFont));
    }

    public Paragraph normalParagraph(String text) {
        return prepareParagraph(new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE)));
    }

    public Paragraph hyperLink(String text, String url) {
        Font underline = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.UNDERLINE, Color.BLUE);
        Anchor hyperlink = new Anchor(text, underline);
        hyperlink.setReference(url);
        return prepareParagraph(new Paragraph(hyperlink));
    }

    public PdfPCell logo() throws IOException {
        Image logo = logoImage();
        logo.scaleToFit(LOGO_WIDTH, LOGO_HEIGHT);
        PdfPCell logoCell = new PdfPCell(logo, false);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setPadding(5);
        return logoCell;
    }

    public PdfPCell title(boolean isInvoice) throws IOException {
        Paragraph title = new Paragraph(
                isInvoice ? INVOICE_TITLE : RECEIPT_TITLE,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setLeading(32f); // double spacing
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_BOTTOM); // 👈 bottom align
        return titleCell;
    }

    public PdfPCell rightHeader(Long subTaskId) {
        Paragraph vat = new Paragraph(VAT_NO, FontFactory.getFont(FontFactory.HELVETICA, 10));
        String paymentRef = subTaskId == null ? NA : "" + subTaskId;

        Paragraph subtask = new Paragraph(PAYMENT_REFERENCE + paymentRef, FontFactory.getFont(FontFactory.HELVETICA, 10));
        PdfPCell rightCell = new PdfPCell();
        rightCell.addElement(vat);
        rightCell.addElement(subtask);
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell.setPadding(5);
        return rightCell;
    }

    public PdfPCell emptyCell() {
        return new PdfPCell(new Phrase(""));
    }

    private Image logoImage() throws IOException {
        // LOGO
        ResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("classpath:" + LOGO_IMAGE_PATH);
        return Image.getInstance(resource.getURI().toURL());
    }

    private Paragraph prepareParagraph(Paragraph para) {
        para.setSpacingBefore(0);
        para.setSpacingAfter(0);
        return para;
    }

    private PdfPCell prepareCell(PdfPCell cell) {
        cell.setPadding(CELL_PADDING);
        return cell;
    }
}
