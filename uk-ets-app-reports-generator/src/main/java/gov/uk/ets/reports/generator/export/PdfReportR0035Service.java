package gov.uk.ets.reports.generator.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import gov.uk.ets.reports.generator.ReportGeneratorException;
import gov.uk.ets.reports.generator.config.PdfReportConfig;
import gov.uk.ets.reports.generator.domain.KyotoAccountType;
import gov.uk.ets.reports.generator.domain.RegistryAccountType;
import gov.uk.ets.reports.generator.domain.ReportData;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class PdfReportR0035Service {
    private final PdfReportConfig config;
    
    private final Map<ReportType, ReportTypeService<ReportData>> reportTypeServiceMap;
    
    public final byte[] writeReport(ReportGenerationCommand command) {
        ReportTypeService<ReportData> reportTypeService = reportTypeServiceMap.get(command.getType());

        if (reportTypeService == null) {
            throw new ReportGeneratorException(String.format("Not supported report type for :%s", command.getType()));
        }
        
        List<ReportData> reportData = 
            reportTypeService.generateReportData(command.getReportQueryInfo());

        Document document = new Document(PageSize.A4.rotate(), 70, 70, 70, 70);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        String dateTimeFormatterPattern = "d MMMM yyyy, h:mma z";
        try {
            PdfWriter.getInstance(document, stream);
            document.open();
            document.addAuthor("Environment Agency");
            document.addTitle("Registry Account Statement of UK Emissions Trading Scheme and UK Kyoto Protocol Registry Transactions");

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
            titleParagraph.add(new Chunk("Registry Account Statement of UK Emissions Trading Scheme and UK Kyoto Protocol Registry Transactions", new Font(Font.HELVETICA, 12, Font.BOLD)));
            document.add(titleParagraph);
            
            Font font = new Font(Font.HELVETICA, 10);
            Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            DateTimeFormatter lastUpdatedFormatter = DateTimeFormatter.ofPattern(dateTimeFormatterPattern).withLocale(Locale.UK).withZone(ZoneId.of("UTC"));
            
            if(Objects.nonNull(reportData) && !reportData.isEmpty()) {
                List<String> reportDataLabels = reportTypeService.getReportHeaders(null);
                List<Object> reportDataRow0 = reportTypeService.getReportDataRow(reportData.get(0));
                //Date & Time of completion: last_updated 4 November 2022 hh:mm
                com.lowagie.text.List listAccountAttrsl = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                listAccountAttrsl.setListSymbol("");
                listAccountAttrsl.add(new ListItem(reportDataLabels.get(0) + " : " + Optional.ofNullable(reportDataRow0.get(0)).orElse("").toString() , font));
                listAccountAttrsl.add(new ListItem(reportDataLabels.get(1) + " : " + Optional.ofNullable(reportDataRow0.get(1)).orElse("").toString() , font));         
                //Filter for Gov accounts
                if(Objects.nonNull(reportDataRow0.get(11)) && 
                    Objects.nonNull(reportDataRow0.get(12)) &&
                    isGovernmentAccount(RegistryAccountType.valueOf(reportDataRow0.get(11).toString()), 
                        KyotoAccountType.valueOf(reportDataRow0.get(12).toString()))) {
                    listAccountAttrsl.add(new ListItem("Account Number : -", font));
                } else {
                    listAccountAttrsl.add(new ListItem("Account Number : " + command.getReportQueryInfo().getAccountFullIdentifier(), font));                   
                }

                document.add(listAccountAttrsl);
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
                document.add(new Chunk("Completed Transactions as at : " +  lastUpdatedFormatter.format(LocalDateTime.now()),font));
                document.add(Chunk.NEWLINE);

                PdfPTable table = new PdfPTable(9);
                table.setWidthPercentage(100);
                //Transaction Completion Date
                table.addCell(createTextCell(reportDataLabels.get(2), boldFont));
                //Transaction ID
                table.addCell(createTextCell(reportDataLabels.get(3), boldFont));
                //Transferring / Acquiring Account ID
                table.addCell(createTextCell(reportDataLabels.get(4), boldFont));
                //Transaction Type
                table.addCell(createTextCell(reportDataLabels.get(5), boldFont));
                //Unit Type
                table.addCell(createTextCell(reportDataLabels.get(6), boldFont));
                //Unit Quantity Incoming (+)
                table.addCell(createTextCell(reportDataLabels.get(7), boldFont));
                //Unit Quantity Outgoing (-)
                table.addCell(createTextCell(reportDataLabels.get(8), boldFont));
                //Project ID (where applicable)
                table.addCell(createTextCell(reportDataLabels.get(9), boldFont));
                //Running Balance 
                table.addCell(createTextCell(reportDataLabels.get(10), boldFont));
                
                //insert data rows
                final var tableRows = reportData.size();
                for (int row = 0; row < tableRows; row++) {
                    List<Object> reportDataRow = reportTypeService.getReportDataRow(reportData.get(row));
     
                    table.addCell(createLocalDateTimeCell((LocalDateTime) reportDataRow.get(2),font));
                    String transactionIdExpression = (String) reportDataRow.get(3);
                    if(reportDataRow.get(16) != null) {
                    	transactionIdExpression += " Original: " + (String) reportDataRow.get(16);
                    }
                    if(reportDataRow.get(17) != null) {
                    	transactionIdExpression += " Reversed By: " + (String) reportDataRow.get(17);
                    }
                    table.addCell(createTextCell(transactionIdExpression,font));
                    //Filter Gov accounts
                    if(isExternalAccount((String) reportDataRow.get(4)) || (Objects.nonNull(reportDataRow.get(13)) && 
                        Objects.nonNull(reportDataRow.get(14)) &&
                        !isGovernmentAccount(RegistryAccountType.valueOf(reportDataRow.get(13).toString()), 
                            KyotoAccountType.valueOf(reportDataRow.get(14).toString())))) {
                        table.addCell(createTextCell((String) reportDataRow.get(4),font));
                    } else if(Objects.nonNull(reportDataRow.get(13)) && Objects.nonNull(reportDataRow.get(14)) 
                        && Objects.nonNull(reportDataRow.get(15)) &&
                        isGovernmentAccount(RegistryAccountType.valueOf(reportDataRow.get(13).toString()), 
                            KyotoAccountType.valueOf(reportDataRow.get(14).toString()))) {
                        table.addCell(createTextCell((String) reportDataRow.get(15),font));
                    } else {
                        table.addCell(createTextCell("-",font));                                       
                    }
                    table.addCell(createTextCell((String) reportDataRow.get(5),font));
                    table.addCell(createTextCell((String) reportDataRow.get(6),font));
                    table.addCell(createNumberCell((Long) reportDataRow.get(7),font));
                    table.addCell(createNumberCell((Long) reportDataRow.get(8),font));
                    table.addCell(createTextCell((String) reportDataRow.get(9),font));
                    table.addCell(createNumberCell((Long) reportDataRow.get(10),font));
                }
                
                document.add(table);                
            } else {
                //Empty No transactions to display
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
                document.add(new Chunk("No Completed Transactions where found as at : " +  lastUpdatedFormatter.format(LocalDateTime.now()),font));
                document.add(Chunk.NEWLINE);
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatterPattern).withLocale(Locale.UK).withZone(ZoneId.of("UTC"));            
            PdfPTable asteriskTable = new PdfPTable(1);
            asteriskTable.setSpacingBefore(50);
            asteriskTable.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase(new Chunk("This statement does not include any pending, terminated, or delayed transactions or transactions awaiting approval. "
                + Chunk.NEWLINE
                + "Please log in to your Registry Account for further details." 
                + Chunk.NEWLINE
                + "Report generated on " + LocalDateTime.now().format(formatter), font)));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.BOX);
            asteriskTable.addCell(cell);
 
            document.add(asteriskTable);
        } catch (DocumentException | IOException ex) {
            log.error("There was an error in generating the PDF document");
        }
        document.close();

        return stream.toByteArray();  
    }
    
    private boolean isExternalAccount(String accountFullIdentifier) {
        return !accountFullIdentifier.startsWith("GB") && !accountFullIdentifier.startsWith("UK");
    }
    
    private boolean isGovernmentAccount(RegistryAccountType registryAccountType,KyotoAccountType kyotoAccountType) {
        return kyotoAccountType.isGovernment() && RegistryAccountType.NONE.equals(registryAccountType) || registryAccountType.isGovernment();
    }
    
    private PdfPCell createTextCell(String text,Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(Optional.ofNullable(text).orElse("").toString(), font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }
    
    private PdfPCell createNumberCell(Long number,Font font) {
        NumberFormat nf = NumberFormat.getInstance(Locale.UK);
        PdfPCell cell = new PdfPCell(new Phrase(nf.format(number), font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }
    
    private PdfPCell createLocalDateTimeCell(LocalDateTime dateTime,Font font) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy").withLocale(Locale.UK).withZone(ZoneId.of("UTC"));
        PdfPCell cell = new PdfPCell(new Phrase(formatter.format(dateTime), font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }
}
