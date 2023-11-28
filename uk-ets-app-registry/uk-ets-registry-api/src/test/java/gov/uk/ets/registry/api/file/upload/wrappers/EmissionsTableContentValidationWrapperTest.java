package gov.uk.ets.registry.api.file.upload.wrappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableError;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsValidityInfo;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;

@TestMethodOrder(OrderAnnotation.class)
class EmissionsTableContentValidationWrapperTest {

	@TempDir
    Path sharedTempDir;
	private final String TEST_FILENAME= "UK_Emissions_28062021_SEPA_98E6BA35A5952E04CC7D8D86B1345237";
	
	private EmissionsTableContentValidationWrapper emissionsTableContentValidationWrapper;
	
	
    @DisplayName("Should validate the excel without errors.")
    @Test
    @Order(1)
    void validateRow() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertTrue(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
		} catch (IOException e) {
			fail(e);
		}
    }
    
    @DisplayName("Should report error 5005 when CompliantEntity ID's do not exist in the registry.")
    @Test
    @Order(2)
    void validateRow_shouldReturn_Error5005() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 54564646777L); //SHOULD BE REPORTED
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5005,e.getError());
				 assertEquals("The Operator does not exist. Enter a valid Operator ID" , e.getErrorMessage());
				 assertEquals(5 , e.getRowNumber());
				 assertEquals(0 , e.getColumnNumber());
			 });		
		} catch (IOException e) {
			fail(e);
		}
    }
    
    @DisplayName("Should report error 5006 when CompliantEntity ID is not linked to an account.")
    @Test
    @Order(3)
    void validateRow_shouldReturn_Error5006() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);  //SHOULD BE REPORTED null AccountStatus
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,null,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(2,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5006,e.getError());
				 assertEquals("The Operator is not linked to an account. Remove the ID from the file or enter a different Operator ID" , e.getErrorMessage());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(6,7),rowIds);
			 assertEquals(List.of(0,0),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }
    
    
    @DisplayName("Should report error 5007 when operator not linked to an account in proper status.")
    @Test
    @Order(4)
    void validateRow_shouldReturn_Error5007() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.TRANSFER_PENDING,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.CLOSED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(4,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5007,e.getError());
				 assertEquals("The Operator ID cannot be added as it is linked to an account with a Closed or Transfer Pending status" , e.getErrorMessage());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(4,5,6,7),rowIds);
			 assertEquals(List.of(0,0,0,0),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }
    
    
    @DisplayName("Should report error 5008 when invalid emissions included for Year.")
    @Test
    @Order(5)
    void validateRow_shouldReturn_Error5008() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, "ABC");
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, -5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 0);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(2,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5008,e.getError());
				 assertEquals("1000012",e.getOperatorId());
				 assertTrue(e.getErrorMessage().contains("The quantity of emissions included for "));
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(4,5),rowIds);
			 assertEquals(List.of(3,3),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }    
    
    @DisplayName("Should report error 5009 when emissions reported for an invalid year.")
    @Test
    @Order(6)
    void validateRow_shouldReturn_Error5009() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, "Two thousand twenty");
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5009,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
				 assertEquals("1000019",e.getOperatorId());
			 });
			 assertEquals(List.of(7),rowIds);
			 assertEquals(List.of(2),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }     
    
    @DisplayName("Should report error 5010 when year outside current phase.")
    @Test
    @Order(7)
    void validateRow_shouldReturn_Error5010() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2018);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2018,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5010,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(7),rowIds);
			 assertEquals(List.of(2),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }         
    
    @DisplayName("Should report error 5011 when invalid year - cannot upload emissions for future years.")
    @Test
    @Order(8)
    void validateRow_shouldReturn_Error5011_ForYear() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2027);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5011,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(6),rowIds);
			 assertEquals(List.of(2),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }
    
    @DisplayName("Should report error 5012 when invalid year - cannot upload emissions for current year unless the Last year of verified emissions = current year.")
    @Test
    @Order(9)
    void validateRow_shouldReturn_Error5012_ForYear() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2021),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5012,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(3),rowIds);
			 assertEquals(List.of(2),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }
    
    @DisplayName("Should report error 5012 when invalid year - cannot upload emissions for current year unless the Last year of verified emissions = current year and last year is empty.")
    @Test
    @Order(9)
    void validateRow_shouldReturn_Error5012_ForYear_when_LastYearOfVerifiedEmissionsEmpty() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,null),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries,2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5012,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(3),rowIds);
			 assertEquals(List.of(2),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }    
    
    @DisplayName("Should report error 5013 when operator not operational for the selected year.")
    @Test
    @Order(10)
    void validateRow_shouldReturn_Error5013() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0,1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2022,2023),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			//Validate the excel
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5013,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(6),rowIds);
			 assertEquals(List.of(2),columnIds);	
		} catch (IOException e) {
			fail(e);
		}
    }    
    
    @DisplayName("Should report error 5014 when empty - missing CompliantEntity ID's.")
    @Test
    @Order(11)
    void validateRow_shouldReturn_Error5014() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, "");//SHOULD BE REPORTED
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, "");//SHOULD BE REPORTED
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, "            ");//SHOULD BE REPORTED
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, "NOT_PARSEABLE");//SHOULD BE REPORTED
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			//Validate the excel
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertTrue(emissionsTableContentValidationWrapper.getFileContentExceptions().size() == 4);
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5014,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(2,3,5,7),rowIds);
			 assertEquals(List.of(0,0,0,0),columnIds);	
		} catch (IOException e) {
			fail(e);
		}
    }
    
    @DisplayName("Should report error 5014 when year is empty - missing.")
    @Test
    @Order(12)
    void validateRow_shouldReturn_Error5014_ForYear() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, "");
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(1,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5014,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(6),rowIds);
			 assertEquals(List.of(2),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }    
    
    @DisplayName("Should report error 5015 when duplicate entries exist in the excel.")
    @Test
    @Order(13)
    void validateRow_shouldReturn_Error5015_ForYear() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2021);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, 2500);
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2021);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of();
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(2,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5015,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(3,7),rowIds);
			 assertEquals(List.of(0,0),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }     
    
    @DisplayName("Should report error 5016 when account is excluded.")
    @Test
    @Order(14)
    void validateRow_shouldReturn_Error5016() {
    	//Prepare the verified emissions excel
		try (OutputStream os = new FileOutputStream(sharedTempDir.resolve(TEST_FILENAME).toFile())) {
		    Workbook wb = new Workbook(os, "MyApplication", "1.0");
		    Worksheet ws = wb.newWorksheet("Sheet 1");
		    //Header
		    ws.value(0, 0, "Operator ID");
		    ws.value(0, 1, "Operator name");
		    ws.value(0, 2, "Year");
		    ws.value(0, 3, "Emissions");
		    //First Data Row
		    ws.value(1, 0, 1000011);
		    ws.value(1, 1, "Test Operator");
		    ws.value(1, 2, 2021);
		    ws.value(1, 3, 1000);
		    //Second Data Row
		    ws.value(2, 0, 1000011);
		    ws.value(2, 1, "");
		    ws.value(2, 2, 2022);
		    ws.value(2, 3, 500);
		    //Third Data Row
		    ws.value(3, 0, 1000012);
		    ws.value(3, 1, "");
		    ws.value(3, 2, 2021);
		    ws.value(3, 3, 0);
		    //Fourth Data Row
		    ws.value(4, 0, 1000012);
		    ws.value(4, 1, "");
		    ws.value(4, 2, 2022);
		    ws.value(4, 3, 5000);
		    //Fifth Data Row
		    ws.value(5, 0, 1000019);
		    ws.value(5, 1, "");
		    ws.value(5, 2, 2021);
		    ws.value(5, 3, "");
		    //Sixth Data Row
		    ws.value(6, 0, 1000019);
		    ws.value(6, 1, "");
		    ws.value(6, 2, 2022);
		    ws.value(6, 3, 4000);
		    wb.finish();
		} catch (IOException e) {
			fail(e);
		}
    	
    	//Prepare "Database Data"
		List<SubmitEmissionsValidityInfo> existingCompliantEntities = List
		        .of(new SubmitEmissionsValidityInfo(1000011L,AccountStatus.OPEN,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000012L,AccountStatus.ALL_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000019L,AccountStatus.SUSPENDED,ComplianceStatus.EXCLUDED,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000020L,AccountStatus.SOME_TRANSACTIONS_RESTRICTED,ComplianceStatus.A,2021,2022),
		        		new SubmitEmissionsValidityInfo(1000021L,AccountStatus.SUSPENDED_PARTIALLY,ComplianceStatus.A,2021,2022));
		List<ExcludeEmissionsEntry> excludeEmissionsEntries = List.of(
			new ExcludeEmissionsEntry(1L, 1000011L, 2021L, true, new Date()),
			new ExcludeEmissionsEntry(2L, 1000011L, 2022L, true, new Date()));
		emissionsTableContentValidationWrapper = new EmissionsTableContentValidationWrapper(
		        existingCompliantEntities, excludeEmissionsEntries, 2022);

		//Read the excel and validate
		try (InputStream is = new FileInputStream(sharedTempDir.resolve(TEST_FILENAME).toFile()); ReadableWorkbook wb = new ReadableWorkbook(is)) {
		    Sheet sheet = wb.getFirstSheet();
			List<Row> rowList = sheet.read();
			
			rowList.
			 stream().
			 skip(1).//Skip the Header
			 forEach(r -> emissionsTableContentValidationWrapper.validateRow(r));
			
			 assertFalse(emissionsTableContentValidationWrapper.getFileContentExceptions().isEmpty());
			 assertEquals(2,emissionsTableContentValidationWrapper.getFileContentExceptions().size());
			 
			 List<Integer> rowIds = new ArrayList<>();
			 List<Integer> columnIds = new ArrayList<>();
			 emissionsTableContentValidationWrapper.getFileContentExceptions().stream().forEach(e -> {
				 assertEquals(EmissionsTableError.ERROR_5016,e.getError());
				 rowIds.add(e.getRowNumber());
				 columnIds.add(e.getColumnNumber());
			 });
			 assertEquals(List.of(2,3),rowIds);
			 assertEquals(List.of(2,2),columnIds);
		} catch (IOException e) {
			fail(e);
		}
    }    
}
