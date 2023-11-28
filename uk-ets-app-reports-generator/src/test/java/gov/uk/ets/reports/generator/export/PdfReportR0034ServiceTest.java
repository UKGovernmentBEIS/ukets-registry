package gov.uk.ets.reports.generator.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import gov.uk.ets.reports.generator.config.PdfReportConfig;
import gov.uk.ets.reports.generator.domain.TransactionDetailsReportData;
import gov.uk.ets.reports.generator.export.standard.TransactionDetailsReportService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.RequestingSystem;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PdfReportConfig.class})
public class PdfReportR0034ServiceTest {

	@InjectMocks
	private PdfReportR0034Service pdfReportR0034Service;

	@Mock
	private TransactionDetailsReportService reportTypeService;
	
	@Autowired
    public PdfReportConfig config;
	
	ReportDataMapper<TransactionDetailsReportData> mapper;

	private TransactionDetailsReportService reportTypeServiceHelper;
	
	private Map<ReportType, TransactionDetailsReportService> reportTypeServiceMap;

	private ReportGenerationCommand command;
	
	private final static String ACQUIRING_ACCOUNT_HOLDER = "Acquiring Account Holder";
	private final static String TRANSFERRING_ACCOUNT_HOLDER = "Transferring Account Holder";

	@BeforeEach
	void setUp() {
		reportTypeServiceHelper = new TransactionDetailsReportService(null);
		command = new ReportGenerationCommand(ReportType.R0034, new ReportQueryInfoWithMetadata(),
				RequestingSystem.REPORTS_API, 1L, LocalDateTime.now());
		reportTypeServiceMap = Map.of(ReportType.R0034, reportTypeService);
		
		ReflectionTestUtils.setField(pdfReportR0034Service, "reportTypeServiceMap", reportTypeServiceMap);
		ReflectionTestUtils.setField(pdfReportR0034Service, "config", config);
	}

	@Test
	void generateReportWithEmptyAccountHolders() throws IOException {
		TransactionDetailsReportData transaction =  TransactionDetailsReportData
	            .builder()
	            .identifier("UK100033")
	            .status("COMPLETED")
	            .lastUpdateDate(LocalDateTime.now())
	            .transferringAccountHolder(null)
	            .transferringFullIdentifier("UK-100-10000072-0-89")
	            .acquiringAccountHolder("")
	            .acquiringFullIdentifier("UK-100-10000072-0-19")
	            .reference("")
	            .type("TransferAllowances")
	            .unitType("ALLOWANCE")
	            .projectNumber("")
	            .applicablePeriod("")
	            .quantity(1L)
	            .build();

		List<TransactionDetailsReportData> reportData = List.of(transaction);

		when(reportTypeService.generateReportData(any(ReportQueryInfoWithMetadata.class))).thenReturn(reportData);
		when(reportTypeService.getReportHeaders(null)).thenReturn(reportTypeServiceHelper.getReportHeaders(null));
		when(reportTypeService.getReportDataRow(reportData.get(0))).thenReturn(reportTypeServiceHelper.getReportDataRow(reportData.get(0)));

		byte[] pdfContent = pdfReportR0034Service.writeReport(command);
		assertThat(pdfContent != null && pdfContent.length > 0).isTrue();
		
		PdfReader reader = new PdfReader(pdfContent);
        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
        String contentOfPDF = pdfTextExtractor.getTextFromPage(1);
        
        assertThat(contentOfPDF)
        	.doesNotContain(ACQUIRING_ACCOUNT_HOLDER)
        	.doesNotContain(TRANSFERRING_ACCOUNT_HOLDER);
	}

	@Test
	void generateReportWithEmptyAcquiringAccountHolder() throws IOException {
		TransactionDetailsReportData transaction =  TransactionDetailsReportData
	            .builder()
	            .identifier("UK100033")
	            .status("COMPLETED")
	            .lastUpdateDate(LocalDateTime.now())
	            .transferringAccountHolder("Transferring Account Holder")
	            .transferringFullIdentifier("UK-100-10000072-0-89")
	            .acquiringAccountHolder(null) //Acquiring Account Holder
	            .acquiringFullIdentifier("UK-100-10000072-0-19")
	            .reference("")
	            .type("TransferAllowances")
	            .unitType("ALLOWANCE")
	            .projectNumber("")
	            .applicablePeriod("")
	            .quantity(1L)
	            .build();

		List<TransactionDetailsReportData> reportData = List.of(transaction);

		when(reportTypeService.generateReportData(any(ReportQueryInfoWithMetadata.class))).thenReturn(reportData);
		when(reportTypeService.getReportHeaders(null)).thenReturn(reportTypeServiceHelper.getReportHeaders(null));
		when(reportTypeService.getReportDataRow(reportData.get(0))).thenReturn(reportTypeServiceHelper.getReportDataRow(reportData.get(0)));

		byte[] pdfContent = pdfReportR0034Service.writeReport(command);
		assertThat(pdfContent != null && pdfContent.length > 0).isTrue();
		
		PdfReader reader = new PdfReader(pdfContent);
        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
        String contentOfPDF = pdfTextExtractor.getTextFromPage(1);
        
        assertThat(contentOfPDF)
        	.doesNotContain(ACQUIRING_ACCOUNT_HOLDER)
        	.contains(TRANSFERRING_ACCOUNT_HOLDER);
	}
	
	@Test
	void generateReport() throws IOException {
		TransactionDetailsReportData transaction =  TransactionDetailsReportData
	            .builder()
	            .identifier("UK100033")
	            .status("COMPLETED")
	            .lastUpdateDate(LocalDateTime.now())
	            .transferringAccountHolder("Transferring Account Holder")
	            .transferringFullIdentifier("UK-100-10000072-0-89")
	            .acquiringAccountHolder("Acquiring Account Holder")
	            .acquiringFullIdentifier("UK-100-10000072-0-19")
	            .reference("")
	            .type("TransferAllowances")
	            .unitType("ALLOWANCE")
	            .projectNumber("")
	            .applicablePeriod("")
	            .quantity(1L)
	            .build();

		List<TransactionDetailsReportData> reportData = List.of(transaction);

		when(reportTypeService.generateReportData(any(ReportQueryInfoWithMetadata.class))).thenReturn(reportData);
		when(reportTypeService.getReportHeaders(null)).thenReturn(reportTypeServiceHelper.getReportHeaders(null));
		when(reportTypeService.getReportDataRow(reportData.get(0))).thenReturn(reportTypeServiceHelper.getReportDataRow(reportData.get(0)));

		byte[] pdfContent = pdfReportR0034Service.writeReport(command);
		assertThat(pdfContent != null && pdfContent.length > 0).isTrue();
		
		PdfReader reader = new PdfReader(pdfContent);
        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
        String contentOfPDF = pdfTextExtractor.getTextFromPage(1);
        
        assertThat(contentOfPDF)
        	.contains(ACQUIRING_ACCOUNT_HOLDER)
        	.contains(TRANSFERRING_ACCOUNT_HOLDER);
	}
}
