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
import gov.uk.ets.reports.generator.domain.AccountTransactionsReportData;
import gov.uk.ets.reports.generator.domain.ReportData;
import gov.uk.ets.reports.generator.export.standard.AccountTransactionsReportService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.RequestingSystem;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PdfReportConfig.class})
public class PdfReportR0035ServiceTest {

	@InjectMocks
	private PdfReportR0035Service pdfReportR0035Service;

	@Mock
	private AccountTransactionsReportService reportTypeService;
	
	@Autowired
    public PdfReportConfig config;
	
	ReportDataMapper<ReportData> mapper;

	private AccountTransactionsReportService reportTypeServiceHelper;
	
	private Map<ReportType, AccountTransactionsReportService> reportTypeServiceMap;

	private ReportGenerationCommand command;

	@BeforeEach
	void setUp() {
		reportTypeServiceHelper = new AccountTransactionsReportService(null);
		command = new ReportGenerationCommand(ReportType.R0035, new ReportQueryInfoWithMetadata(),
				RequestingSystem.REPORTS_API, 1L, LocalDateTime.now());
		reportTypeServiceMap = Map.of(ReportType.R0035, reportTypeService);
		
		ReflectionTestUtils.setField(pdfReportR0035Service, "reportTypeServiceMap", reportTypeServiceMap);
		ReflectionTestUtils.setField(pdfReportR0035Service, "config", config);
	}

	@Test
	void generateReportIncludingReversalTransaction() throws IOException {
		AccountTransactionsReportData reversalTransaction =  AccountTransactionsReportData
	            .builder()
	            .accountName("British Air Wings")
	            .accountHolderName("British Wings")
	            .completionDate(LocalDateTime.now())
	            .transactionIdentifier("UK100041")
	            .otherAccountIdentifier("UK-100-10000025-0-33")
	            .transactionType("ReverseSurrenderAllowances")
	            .unitType("ALLOWANCE")
	            .unitQuantityIncoming(1L)
	            .unitQuantityOutgoing(0L)
	            .projectId("Not Applicable")
	            .runningBalance(10L)
	            .reportRegistryAccountType("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT")
	            .reportKyotoAccountType("PARTY_HOLDING_ACCOUNT")
	            .otherRegistryAccountType("UK_SURRENDER_ACCOUNT")
	            .otherKyotoAccountType("PARTY_HOLDING_ACCOUNT")
	            .otherAccountName("UK Surrender Account")
	            .reversesIdentifier("UK100019")
	            .reversedByIdentifier(null)
	            .build();

		List<AccountTransactionsReportData> reportData = List.of(reversalTransaction);

		when(reportTypeService.generateReportData(any(ReportQueryInfoWithMetadata.class))).thenReturn(reportData);
		when(reportTypeService.getReportHeaders(null)).thenReturn(reportTypeServiceHelper.getReportHeaders(null));
		when(reportTypeService.getReportDataRow(reportData.get(0))).thenReturn(reportTypeServiceHelper.getReportDataRow(reportData.get(0)));

		byte[] pdfContent = pdfReportR0035Service.writeReport(command);
		assertThat(pdfContent != null && pdfContent.length > 0).isTrue();
		
		PdfReader reader = new PdfReader(pdfContent);
        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
        String contentOfPDF = pdfTextExtractor.getTextFromPage(1);
        
        assertThat(contentOfPDF)
        	.contains("Original:")
        	.doesNotContain("Reversed By:");
	}

	@Test
	void generateReportIncludingReversedTransaction() throws IOException {
		AccountTransactionsReportData reversedTransaction =  AccountTransactionsReportData
	            .builder()
	            .accountName("British Air Wings")
	            .accountHolderName("British Wings")
	            .completionDate(LocalDateTime.now())
	            .transactionIdentifier("UK100041")
	            .otherAccountIdentifier("UK-100-10000025-0-33")
	            .transactionType("ReverseSurrenderAllowances")
	            .unitType("ALLOWANCE")
	            .unitQuantityIncoming(1L)
	            .unitQuantityOutgoing(0L)
	            .projectId("Not Applicable")
	            .runningBalance(10L)
	            .reportRegistryAccountType("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT")
	            .reportKyotoAccountType("PARTY_HOLDING_ACCOUNT")
	            .otherRegistryAccountType("UK_SURRENDER_ACCOUNT")
	            .otherKyotoAccountType("PARTY_HOLDING_ACCOUNT")
	            .otherAccountName("UK Surrender Account")
	            .reversesIdentifier(null)
	            .reversedByIdentifier("UK100019")
	            .build();

		List<AccountTransactionsReportData> reportData = List.of(reversedTransaction);

		when(reportTypeService.generateReportData(any(ReportQueryInfoWithMetadata.class))).thenReturn(reportData);
		when(reportTypeService.getReportHeaders(null)).thenReturn(reportTypeServiceHelper.getReportHeaders(null));
		when(reportTypeService.getReportDataRow(reportData.get(0))).thenReturn(reportTypeServiceHelper.getReportDataRow(reportData.get(0)));

		byte[] pdfContent = pdfReportR0035Service.writeReport(command);
		assertThat(pdfContent != null && pdfContent.length > 0).isTrue();
		
		PdfReader reader = new PdfReader(pdfContent);
        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
        String contentOfPDF = pdfTextExtractor.getTextFromPage(1);
        
        assertThat(contentOfPDF)
        	.doesNotContain("Original:")
        	.contains("Reversed By:");
	}

	@Test
	void generateReport() throws IOException {
		AccountTransactionsReportData transaction =  AccountTransactionsReportData
	            .builder()
	            .accountName("British Air Wings")
	            .accountHolderName("British Wings")
	            .completionDate(LocalDateTime.now())
	            .transactionIdentifier("UK100041")
	            .otherAccountIdentifier("UK-100-10000025-0-33")
	            .transactionType("ReverseSurrenderAllowances")
	            .unitType("ALLOWANCE")
	            .unitQuantityIncoming(1L)
	            .unitQuantityOutgoing(0L)
	            .projectId("Not Applicable")
	            .runningBalance(10L)
	            .reportRegistryAccountType("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT")
	            .reportKyotoAccountType("PARTY_HOLDING_ACCOUNT")
	            .otherRegistryAccountType("UK_SURRENDER_ACCOUNT")
	            .otherKyotoAccountType("PARTY_HOLDING_ACCOUNT")
	            .otherAccountName("UK Surrender Account")
	            .reversesIdentifier(null)
	            .reversedByIdentifier(null)
	            .build();

		List<AccountTransactionsReportData> reportData = List.of(transaction);

		when(reportTypeService.generateReportData(any(ReportQueryInfoWithMetadata.class))).thenReturn(reportData);
		when(reportTypeService.getReportHeaders(null)).thenReturn(reportTypeServiceHelper.getReportHeaders(null));
		when(reportTypeService.getReportDataRow(reportData.get(0))).thenReturn(reportTypeServiceHelper.getReportDataRow(reportData.get(0)));

		byte[] pdfContent = pdfReportR0035Service.writeReport(command);
		assertThat(pdfContent != null && pdfContent.length > 0).isTrue();
		
		PdfReader reader = new PdfReader(pdfContent);
        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
        String contentOfPDF = pdfTextExtractor.getTextFromPage(1);
        
        assertThat(contentOfPDF)
        	.doesNotContain("Original:")
        	.doesNotContain("Reversed By:");
	}
}
