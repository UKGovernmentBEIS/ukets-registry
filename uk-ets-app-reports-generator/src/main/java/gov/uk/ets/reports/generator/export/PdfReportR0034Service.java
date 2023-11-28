package gov.uk.ets.reports.generator.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import gov.uk.ets.reports.generator.ReportGeneratorException;
import gov.uk.ets.reports.generator.config.PdfReportConfig;
import gov.uk.ets.reports.generator.domain.ReportData;
import gov.uk.ets.reports.generator.domain.TransactionType;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class PdfReportR0034Service {

    private final PdfReportConfig config;
    
    private final Map<ReportType, ReportTypeService<ReportData>> reportTypeServiceMap;
        
    public final byte[] writeReport(ReportGenerationCommand command) {
        ReportTypeService<ReportData> reportTypeService = reportTypeServiceMap.get(command.getType());

        if (reportTypeService == null) {
            throw new ReportGeneratorException(String.format("Not supported report type for :%s", command.getType()));
        }

        List<ReportData> reportData = 
                reportTypeService.generateReportData(command.getReportQueryInfo());
        List<String> reportDataLabels = reportTypeService.getReportHeaders(null);
        List<ReportUnit> units = reportData
                                       .stream()
                                       .map(reportTypeService::getReportDataRow)
                                       .map(row -> new ReportUnit(row.get(9),row.get(10),row.get(11),row.get(12)))
                                       .toList();
        List<Object> reportDataRow = reportTypeService.getReportDataRow(reportData.get(0));
        Document document = new Document(PageSize.A4, 70, 70, 70, 70);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        String dateTimeFormatterPattern = "d MMMM yyyy, h:mma z";
        try {
        	PdfWriter pdfWriter = PdfWriter.getInstance(document, stream);
            document.open();
            document.addAuthor("Environment Agency");
            document.addTitle("Transaction Details");

            // LOGO
            ResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:" + config.getLogoPath());
            Image logo = Image.getInstance(resource.getURI().toURL());
            logo.scalePercent(50);
            logo.setAlignment(Image.RIGHT);
            document.add(logo);

            Paragraph titleParagraph = new Paragraph();
            titleParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            titleParagraph.setSpacingBefore(30);
            titleParagraph.setSpacingAfter(10);
            titleParagraph.add(new Chunk("Transaction Details", new Font(Font.HELVETICA, 15, Font.BOLD)));
            document.add(titleParagraph);
            
            Font font = new Font(Font.HELVETICA, 11);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            
            //Date & Time of completion: last_updated 4 November 2022 hh:mm
            DateTimeFormatter lastUpdatedFormatter = DateTimeFormatter.ofPattern(dateTimeFormatterPattern).withLocale(Locale.UK).withZone(ZoneId.of("UTC"));
            com.lowagie.text.List listTransactionAttrsl = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
            listTransactionAttrsl.setListSymbol("");
            listTransactionAttrsl.add(new ListItem(reportDataLabels.get(0) + ": " + reportDataRow.get(0).toString() , font));
            //Transaction Type
            TransactionType transactionType = TransactionType.valueOf(reportDataRow.get(1).toString());
            listTransactionAttrsl.add(new ListItem(reportDataLabels.get(1) + ": " + transactionType.getDefaultLabel() , font));
            listTransactionAttrsl.add(new ListItem(reportDataLabels.get(2) + ": " + Optional.ofNullable(reportDataRow.get(2)).orElse(""), font));
            listTransactionAttrsl.add(new ListItem(reportDataLabels.get(3) + ": " +  lastUpdatedFormatter.format((LocalDateTime)reportDataRow.get(3)) , font));
            //Remove from now as this requires further analysis.
            if(reportDataRow.get(4) != null && !"".equals(StringUtils.trim(String.valueOf(reportDataRow.get(4))))) {
            	listTransactionAttrsl.add(new ListItem(reportDataLabels.get(4) + ": " + reportDataRow.get(4), font));
            }
            listTransactionAttrsl.add(new ListItem(reportDataLabels.get(5) + ": " + reportDataRow.get(5).toString() , font));
            if(reportDataRow.get(6) != null && !"".equals(StringUtils.trim(String.valueOf(reportDataRow.get(6))))) {
            	listTransactionAttrsl.add(new ListItem(reportDataLabels.get(6) + ": " + reportDataRow.get(6), font));
            }
            listTransactionAttrsl.add(new ListItem(reportDataLabels.get(7) + ": " + reportDataRow.get(7).toString() , font));
            listTransactionAttrsl.add(new ListItem(reportDataLabels.get(8) + ": " + Optional.ofNullable(reportDataRow.get(8)).orElse(""), font));
            
            document.add(listTransactionAttrsl);

            // UNITS
            Paragraph unitsParagraph = new Paragraph();
            unitsParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            unitsParagraph.setSpacingBefore(30);
            unitsParagraph.setSpacingAfter(10);
            unitsParagraph.add(new Chunk("Units", new Font(Font.HELVETICA, 15, Font.BOLD)));
            document.add(unitsParagraph);
            
            document.add(new Paragraph());
            
            Phrase footNote = new Phrase(new Chunk("*Applicable to specific transaction and unit types only", font));
            addPdfFootNote(pdfWriter, document, footNote);
            
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase(reportDataLabels.get(9), boldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.BOX);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(reportDataLabels.get(10), boldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.BOX);
            table.addCell(cell);
            //Project Number
            cell = new PdfPCell(new Phrase(reportDataLabels.get(11), boldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.BOX);
            table.addCell(cell);
            //Applicable CP
            cell = new PdfPCell(new Phrase(reportDataLabels.get(12), boldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.BOX);
            table.addCell(cell);
            
            //insert data rows
            for (ReportUnit unit :units) {
                cell = new PdfPCell(new Phrase(unit.getUnitType() != null ? unit.getUnitType().toString() : "", font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.BOX);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(unit.getQuantity() != null ? unit.getQuantity().toString() : "", font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.BOX);
                table.addCell(cell);
                
                String projectNumber = "";
                if(transactionType.isETSTransaction()) {
                    projectNumber = "Not Applicable";
                } else if (Optional.ofNullable(unit.getProjectNumber()).isPresent()) {
                    projectNumber = unit.getProjectNumber().toString();
                }
                
                cell = new PdfPCell(new Phrase(projectNumber, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.BOX);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase(transactionType.isETSTransaction() ? "Not Applicable" : unit.getApplicablePeriod().toString(), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.BOX);
                table.addCell(cell);
            }
            document.add(table);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatterPattern).withLocale(Locale.UK).withZone(ZoneId.of("UTC"));
            Paragraph generatedOnParagraph = new Paragraph();
            generatedOnParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            generatedOnParagraph.setSpacingBefore(30);
            generatedOnParagraph.setSpacingAfter(10);
            generatedOnParagraph.add(new Chunk("Report generated on " + LocalDateTime.now().format(formatter), font));
            document.add(generatedOnParagraph);            
 
        } catch (DocumentException | IOException ex) {
            log.error("There was an error in generating the PDF document");
        }
        document.close();

        return stream.toByteArray();

    }   
    
    private void addPdfFootNote(PdfWriter writer, Document document, Phrase phrase) {
		PdfContentByte cb;
		cb = writer.getDirectContent();
		cb.moveTo(document.leftMargin(), document.bottom() - 8);
		cb.lineTo(document.leftMargin() + 200, document.bottom() - 8);
		ColumnText.showTextAligned(cb, 
				Element.ALIGN_LEFT, 
				phrase,
				document.leftMargin(),
				document.bottom() - 18, 0);
	}
    
    @AllArgsConstructor
    @Getter
    private class ReportUnit {
        Object unitType;
        Object quantity;
        Object projectNumber;
        Object applicablePeriod;
    }
}
