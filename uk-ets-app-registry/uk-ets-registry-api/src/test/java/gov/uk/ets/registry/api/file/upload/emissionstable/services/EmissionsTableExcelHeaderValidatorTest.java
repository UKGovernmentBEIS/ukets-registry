package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ooxml.POIXMLException;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableError;

class EmissionsTableExcelHeaderValidatorTest {

    @MockBean
    private CompliantEntityRepository compliantEntityRepository;
    private MockMultipartFile file;
    
    private EmissionsTableExcelHeaderValidator emissionsTableExcelHeaderValidator;
    private Resource resource = new ClassPathResource("UK_Emissions_28062021_SEPA_98E6BA35A5952E04CC7D8D86B1345237.xlsx");
    
    @BeforeEach
    void setUp() throws IOException {
        file = new MockMultipartFile(
                "file",
                requireNonNull(resource.getFilename()).substring(0, resource.getFilename().lastIndexOf(".")),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                resource.getInputStream().readAllBytes()
            );
    }
    
    @Test
    @DisplayName("Test Valid column Headers.")
    void validateFileHeadersBusinessRules() {
    	
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes());ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
    		Sheet sheet = wb.getFirstSheet();
    		List<Row> sheetList = sheet.read();
    		emissionsTableExcelHeaderValidator = new EmissionsTableExcelHeaderValidator(sheetList.get(0));
            emissionsTableExcelHeaderValidator.validate();
        } catch (POIXMLException | IOException exception) {
            fail("Could not read test excel file.");
        }
    	
    }
    
    @Test
    @DisplayName("Test invalid Operator ID column Header.")
    void validateFileHeadersBusinessRulesShouldThrowExceptionForInvalidOperatorID() {
    	
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes());ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
    		Sheet sheet = wb.getFirstSheet();
    		List<Row> sheetList = sheet.read();
    		List<Cell> cells = sheetList.get(0).getCells(0,sheetList.get(0).getCellCount());
    		final int operatorIdColumnIndex = 0 ;
    		cells.remove(operatorIdColumnIndex);
    		emissionsTableExcelHeaderValidator = new EmissionsTableExcelHeaderValidator(sheetList.get(0));
    		
    		EmissionsTableBusinessRulesException exception = assertThrows(EmissionsTableBusinessRulesException.class, () -> {
                emissionsTableExcelHeaderValidator.validate();
            });

            assertTrue(exception.getEmissionsTableErrorList().stream().map(t->t.getError()).findFirst().get().equals(EmissionsTableError.ERROR_5003));
            
        } catch (POIXMLException | IOException exception) {
            fail("Could not read test excel file.");
        }
    	
    }
    
    @Test
    @DisplayName("Test invalid Year column Header.")
    void validateFileHeadersBusinessRulesShouldThrowExceptionForInvalidYear() {
    	
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes());ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
    		Sheet sheet = wb.getFirstSheet();
    		List<Row> sheetList = sheet.read();
    		List<Cell> cells = sheetList.get(0).getCells(0,sheetList.get(0).getCellCount());
    		final int yearColumnIndex = 2 ;
    		cells.remove(yearColumnIndex);
    		emissionsTableExcelHeaderValidator = new EmissionsTableExcelHeaderValidator(sheetList.get(0));
    		
    		EmissionsTableBusinessRulesException exception = assertThrows(EmissionsTableBusinessRulesException.class, () -> {
                emissionsTableExcelHeaderValidator.validate();
            });

            assertTrue(exception.getEmissionsTableErrorList().stream().map(t->t.getError()).findFirst().get().equals(EmissionsTableError.ERROR_5003));
            
        } catch (POIXMLException | IOException exception) {
            fail("Could not read test excel file.");
        }
    	
    }
    
    @Test
    @DisplayName("Test invalid Emissions column Header.")
    @Disabled
    void validateFileHeadersBusinessRulesShouldThrowExceptionForInvalidEmissions() {
    	
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes());ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
    		Sheet sheet = wb.getFirstSheet();
    		List<Row> sheetList = sheet.read();
    		List<Cell> cells = sheetList.get(0).getCells(0,sheetList.get(0).getCellCount());
    		final int emissionsColumnIndex = 3 ;
    		cells.remove(emissionsColumnIndex);
    		emissionsTableExcelHeaderValidator = new EmissionsTableExcelHeaderValidator(sheetList.get(0));
    		
    		EmissionsTableBusinessRulesException exception = assertThrows(EmissionsTableBusinessRulesException.class, () -> {
                emissionsTableExcelHeaderValidator.validate();
            });

            assertTrue(exception.getEmissionsTableErrorList().stream().map(t->t.getError()).findFirst().get().equals(EmissionsTableError.ERROR_5003));
            
        } catch (POIXMLException | IOException exception) {
            fail("Could not read test excel file.");
        }
    	
    }
}
