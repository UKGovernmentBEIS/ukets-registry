package gov.uk.ets.registry.api.account.service.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeaderAndFooterPageEventHelper extends PdfPageEventHelper {

    private final DocumentHelper helper;

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        // Show header only on first page
        if (document.getPageNumber() == 1) {

            PdfPTable table = new PdfPTable(1);
            table.setTotalWidth(DocumentHelper.CONTENT_WIDTH);
            table.setWidthPercentage(DocumentHelper.TABLE_WIDTH_PERCENTAGE);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Top line
            PdfPCell topLineCell = new PdfPCell(new Paragraph(""));
            topLineCell.setPadding(5);
            topLineCell.setBorder(Rectangle.NO_BORDER);
            topLineCell.setBackgroundColor(DocumentHelper.APP_BLUE);
            table.addCell(topLineCell);

            PdfPCell emptyCell = new PdfPCell(new Paragraph(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);


            // Row#1 having 1 empty cell, 1 title cell and empty cell.
            table.addCell(emptyCell);
            Paragraph title = new Paragraph("Open account request",
                new Font(helper.getAppFont().getFamily(), 20, Font.BOLD));
            PdfPCell titleCell = new PdfPCell(title);
            titleCell.setPaddingTop(20);
            titleCell.setPaddingBottom(25);
            titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            titleCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(titleCell);
            table.addCell(emptyCell);

            //Bottom line
            PdfPCell bottomLineCell = new PdfPCell(new Paragraph(""));
            bottomLineCell.setBackgroundColor(DocumentHelper.APP_BLUE);
            bottomLineCell.setBorder(Rectangle.NO_BORDER);
            bottomLineCell.setFixedHeight(1);
            bottomLineCell.setPaddingBottom(1);
            table.addCell(bottomLineCell);

            table.writeSelectedRows(0, -1, 34, 843, writer.getDirectContent());
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(DocumentHelper.TABLE_WIDTH);
        table.getDefaultCell().setBackgroundColor(DocumentHelper.APP_LIGHT_GREY);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Paragraph pageNumberText = new Paragraph("Page " + document.getPageNumber(),
            new Font(helper.getAppFont().getFamily(), 8, -1, DocumentHelper.APP_GREY));
        PdfPCell pageNumberCell = new PdfPCell(pageNumberText);
        pageNumberCell.setPadding(10);
        pageNumberCell.setPaddingRight(40);
        pageNumberCell.setBorder(Rectangle.NO_BORDER);
        pageNumberCell.setBackgroundColor(DocumentHelper.APP_LIGHT_GREY);
        pageNumberCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(pageNumberCell);

        // write the table on PDF
        table.writeSelectedRows(0, -1, 0, 28, writer.getDirectContent());
    }
}