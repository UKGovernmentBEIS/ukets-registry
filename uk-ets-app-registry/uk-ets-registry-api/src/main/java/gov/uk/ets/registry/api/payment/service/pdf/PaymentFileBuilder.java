package gov.uk.ets.registry.api.payment.service.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.service.pdf.converters.PaymentToInvoiceDataConverter;
import gov.uk.ets.registry.api.payment.service.pdf.utils.BillInfoUtils;
import gov.uk.ets.registry.api.payment.service.pdf.utils.HelpInfoUtils;
import gov.uk.ets.registry.api.payment.service.pdf.utils.HowToPayUtils;
import gov.uk.ets.registry.api.payment.service.pdf.utils.PaymentTableUtils;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;

import static gov.uk.ets.registry.api.payment.service.pdf.PaymentInvoicePdfUtils.RECEIPT_FOR_INFO;

@Service
@RequiredArgsConstructor
public class PaymentFileBuilder {
	
    private final PdfFormatter pdfFormatter;
    private final HowToPayUtils howToPayUtils;
    private final HelpInfoUtils helpInfoUtils;
    private final BillInfoUtils billInfoUtils;
    private final PaymentTableUtils paymentTableUtils;
    private final PaymentToInvoiceDataConverter paymentConverter;

    public byte[] createInvoicePdf(Long requestId, PaymentDTO paymentDTO, Long subTaskId) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        setPdfContent(document, requestId, paymentDTO, subTaskId, null);
        document.close();
        return outputStream.toByteArray();
    }

    public byte[] createReceiptPdf(Payment payment) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        setReceiptPdfContent(document, payment);
        document.close();
        return outputStream.toByteArray();
    }

    public byte[] createInvoicePdf(Task paymentSubtask, PaymentDTO paymentDTO, String paymentLink, Long paymentSubtaskId) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        setPdfContent(
                document,
                paymentSubtask.getParentTask().getRequestId(),
                paymentDTO,
                paymentSubtaskId,
                paymentLink
            );
        document.close();
        return outputStream.toByteArray();
    }

    private void setPdfContent(Document document,
                               Long parentTaskRequestId,
                               PaymentDTO payment,
                               Long paymentSubtaskId,
                               String paymentLink
                               ) throws Exception {
        addHeaderLayout(document, paymentSubtaskId, true);
        addBillingInfo(document, parentTaskRequestId, true);
        addPaymentTable(document);
        document.add(
                paymentTableUtils.addPaymentTable(
                        paymentConverter.convert(payment),
                        paymentConverter.total(payment))
        );
        document.add(Chunk.NEWLINE);
        document.add(howToPayUtils.buildPaymentDetailsTable(paymentLink));
        document.add(Chunk.NEWLINE);
        document.add(helpInfoUtils.addHelpInfoDetails());
    }

    private void addHeaderLayout(Document document, Long subtaskId, boolean isInvoice) throws Exception {
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{4, 4, 4}); // logo | title | right-side
        headerTable.addCell(pdfFormatter.logo());
        headerTable.addCell(pdfFormatter.title(isInvoice));
        headerTable.addCell(pdfFormatter.rightHeader(subtaskId));
        document.add(headerTable);
    }

    private void addPaymentTable(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{3f, 1f, 1f, 1f});
        document.add(table);
    }

    private void setReceiptPdfContent(Document document, Payment payment) throws Exception {
        addHeaderLayout(document, payment.getReferenceNumber(), false);
        addBillingInfo(document, payment.getReferenceNumber(), false);
        document.add(Chunk.NEWLINE);

        document.add(pdfFormatter.normalParagraph(RECEIPT_FOR_INFO));
        document.add(Chunk.NEWLINE);

        addPaymentTable(document);
        document.add(
                paymentTableUtils.addPaymentTable(
                        paymentConverter.convert(payment),
                        paymentConverter.total(payment))
        );
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(helpInfoUtils.addAdditionalInfoRecipe());
    }

    private void addBillingInfo(Document document, Long parentTaskRequestId, boolean isInvoice) throws Exception {
        addBillingInfo(document, billInfoUtils.getBillingParagraph(parentTaskRequestId, isInvoice));
    }

    private void addBillingInfo(Document document, Paragraph paragraph) {
        PdfPTable boxTable = new PdfPTable(1);
        boxTable.setWidthPercentage(100);

        PdfPCell borderedCell = new PdfPCell(paragraph);
        borderedCell.setBorder(Rectangle.BOX);
        borderedCell.setBorderWidth(1f);
        borderedCell.setPadding(8f);

        boxTable.addCell(borderedCell);

        Paragraph wrapper = new Paragraph();
        wrapper.setSpacingBefore(15f);  // top margin
        wrapper.setSpacingAfter(15f);   // bottom margin
        wrapper.add(boxTable);

        // Add to document
        document.add(wrapper);
    }
}
