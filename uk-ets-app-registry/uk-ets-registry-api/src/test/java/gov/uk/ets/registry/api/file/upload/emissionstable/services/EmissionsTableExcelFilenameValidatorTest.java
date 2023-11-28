package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableError;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableExcelFilenameValidator.EmissionsFilenameRegExpGroup;
import gov.uk.ets.registry.api.file.upload.error.FileNameNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class EmissionsTableExcelFilenameValidatorTest {

    private EmissionsTableExcelFilenameValidator emissionsTableExcelFilenameValidator;
	
    @ParameterizedTest
    @EnumSource(value = RegulatorType.class, mode = EnumSource.Mode.EXCLUDE, names = { "BEIS_OPRED" })
    @DisplayName("Test all regulators in Emissions Table filename validation")
    void validateFileNameAllRegulators(RegulatorType regulator) {
        String filename = "UK_Emissions_28062021_" + regulator.name() + "_83BBCD0F874C2E436A5E6DA772AA2822";

    	Map<EmissionsFilenameRegExpGroup,String> expected = new EnumMap<>(EmissionsFilenameRegExpGroup.class);
    	expected.put(EmissionsFilenameRegExpGroup.FILENAME, filename.toUpperCase());
    	expected.put(EmissionsFilenameRegExpGroup.FILENAME_PREFIX, "UK_EMISSIONS");
    	expected.put(EmissionsFilenameRegExpGroup.CONTENT_DATE, "28062021");
    	expected.put(EmissionsFilenameRegExpGroup.REGULATOR, regulator.name());
    	expected.put(EmissionsFilenameRegExpGroup.MD5_CHECKSUM, "83BBCD0F874C2E436A5E6DA772AA2822");
        emissionsTableExcelFilenameValidator = new EmissionsTableExcelFilenameValidator(filename);
        assertEquals(expected,emissionsTableExcelFilenameValidator.getRegExpGroupsMap());
        //Also test lower case
        emissionsTableExcelFilenameValidator = new EmissionsTableExcelFilenameValidator("UK_Emissions_28062021_" + regulator.name().toLowerCase() + "_83BBCD0F874C2E436A5E6DA772AA2822");
        assertEquals(expected,emissionsTableExcelFilenameValidator.getRegExpGroupsMap());
    }
    
    @DisplayName("Test regulator OPRED in Emissions Table filename validation")
    void validateFileNameRegulator_BEIS_DASH_OPRED() {
        
        String filename = "UK_Emissions_28062021_OPRED_83BBCD0F874C2E436A5E6DA772AA2822";
        
        Map<EmissionsFilenameRegExpGroup,String> expected = new EnumMap<>(EmissionsFilenameRegExpGroup.class);
        expected.put(EmissionsFilenameRegExpGroup.FILENAME, filename.toUpperCase());
        expected.put(EmissionsFilenameRegExpGroup.FILENAME_PREFIX, "UK_EMISSIONS");
        expected.put(EmissionsFilenameRegExpGroup.CONTENT_DATE, "28062021");
        expected.put(EmissionsFilenameRegExpGroup.REGULATOR, "OPRED");
        expected.put(EmissionsFilenameRegExpGroup.MD5_CHECKSUM, "83BBCD0F874C2E436A5E6DA772AA2822");

        emissionsTableExcelFilenameValidator = new EmissionsTableExcelFilenameValidator(filename);
        assertEquals(expected,emissionsTableExcelFilenameValidator.getRegExpGroupsMap());
        //Also test lower case
        emissionsTableExcelFilenameValidator = new EmissionsTableExcelFilenameValidator("UK_Emissions_28062021_OPRED_83BBCD0F874C2E436A5E6DA772AA2822");
        assertEquals(expected,emissionsTableExcelFilenameValidator.getRegExpGroupsMap());
    }
    
    @Test
    @DisplayName("Test filename validation with optional version suffix (v1.0a)")
    void validateFileNameAllRegulators() {
    	
    	String filename = "UK_Emissions_28062021_" + RegulatorType.EA.name() + "_83BBCD0F874C2E436A5E6DA772AA2822_v10a";
    	
    	Map<EmissionsFilenameRegExpGroup,String> expected = new EnumMap<>(EmissionsFilenameRegExpGroup.class);
    	expected.put(EmissionsFilenameRegExpGroup.FILENAME, filename.toUpperCase());
    	expected.put(EmissionsFilenameRegExpGroup.FILENAME_PREFIX, "UK_EMISSIONS");
    	expected.put(EmissionsFilenameRegExpGroup.CONTENT_DATE, "28062021");
    	expected.put(EmissionsFilenameRegExpGroup.REGULATOR, RegulatorType.EA.name());
    	expected.put(EmissionsFilenameRegExpGroup.MD5_CHECKSUM, "83BBCD0F874C2E436A5E6DA772AA2822");

    	emissionsTableExcelFilenameValidator = new EmissionsTableExcelFilenameValidator(filename);
    	assertEquals(expected,emissionsTableExcelFilenameValidator.getRegExpGroupsMap());
    }
    
    @Test
    @DisplayName("Test a valid MD5")
    void validateMD5Checksum() {
    	
		String filename = "UK_Emissions_28062021_" + RegulatorType.EA.name() + "_83BBCD0F874C2E436A5E6DA772AA2822_v10a";
		emissionsTableExcelFilenameValidator = new EmissionsTableExcelFilenameValidator(filename);
	    assertDoesNotThrow(() -> {
		    emissionsTableExcelFilenameValidator.validateMD5Checksum("83BBCD0F874C2E436A5E6DA772AA2822");
		});
    }
    
    @Test
    @DisplayName("Test a different MD5 from the one reported")
    void invalidMD5Checksum() {
    	
		String filename = "UK_Emissions_28062021_" + RegulatorType.EA.name() + "_83BBCD0F874C2E436A5E6DA772AA2822_v10a";
		emissionsTableExcelFilenameValidator = new EmissionsTableExcelFilenameValidator(filename);
        Exception exception = assertThrows(FileNameNotValidException.class, () -> {
        	emissionsTableExcelFilenameValidator.validateMD5Checksum("83BBCD0F874C2E436A5E6DA772AA2833");
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(EmissionsTableError.ERROR_5004.getMessageTemplate()));
    	
    }
    
    @Test
    @DisplayName("Test invalid start of filename , GB instead of UK.")
    void validateFileNameShouldThrowExceptionForInvalidStartName() {
    	
        Exception exception = assertThrows(FileNameNotValidException.class, () -> {
        	new EmissionsTableExcelFilenameValidator("GB_Emissions_28062021_" + RegulatorType.OPRED.name() + "_83BBCD0F874C2E436A5E6DA772AA2822");
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(EmissionsTableError.ERROR_5002.getMessageTemplate()));
    	
    }
    
    @Test
    @DisplayName("Test invalid date of filename.")
    void validateFileNameShouldThrowExceptionForInvalidDate() {
    	
        Exception exception = assertThrows(FileNameNotValidException.class, () -> {
        	new EmissionsTableExcelFilenameValidator("GB_Emissions_280620ZZ_" + RegulatorType.DAERA.name() + "_83BBCD0F874C2E436A5E6DA772AA2822");
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(EmissionsTableError.ERROR_5002.getMessageTemplate()));
    	
    }
    
    @Test
    @DisplayName("Test invalid regulator of filename.")
    void validateFileNameShouldThrowExceptionForInvalidRegulator() {
    	
        Exception exception = assertThrows(FileNameNotValidException.class, () -> {
        	new EmissionsTableExcelFilenameValidator("GB_Emissions_28062021_INVALID_83BBCD0F874C2E436A5E6DA772AA2822");
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(EmissionsTableError.ERROR_5002.getMessageTemplate()));
    	
    }
    
    @Test
    @DisplayName("Test invalid MD5 hash of filename(31 chars).")
    void validateFileNameShouldThrowExceptionForInvalidMD5() {
    	
        Exception exception = assertThrows(FileNameNotValidException.class, () -> {
        	new EmissionsTableExcelFilenameValidator("GB_Emissions_28062021_" + RegulatorType.NRW.name() + "_83BBCD0F874C2E436A5E6DA772AA282");
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(EmissionsTableError.ERROR_5002.getMessageTemplate()));
    	
    }	
	
}
