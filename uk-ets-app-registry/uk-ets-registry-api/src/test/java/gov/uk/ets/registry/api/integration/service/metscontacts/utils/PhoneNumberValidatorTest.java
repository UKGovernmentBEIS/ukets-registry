package gov.uk.ets.registry.api.integration.service.metscontacts.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.ArrayList;
import java.util.List;

class PhoneNumberValidatorTest {

    private final PhoneNumberValidator validator = new PhoneNumberValidator();

    @Test
    void mandatory_blankPhone_addsOnly0702_andDoesNotAdd0701() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        validator.validateMandatoryPhone(
                "+44",
                "   ",// blank phone
                "telephoneCountryCode",
                "telephoneNumber",
                errors,
                false
        );

        assertEquals(1, errors.size(), "Should add exactly one error");
        assertEquals(IntegrationEventError.ERROR_0702, errors.get(0).getError());
        assertEquals("telephoneNumber", errors.get(0).getErrorMessage());
    }

    @Test
    void optional_bothBlank_addsNoErrors() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        validator.validateOptionalPhone(
                "   ",
                null,
                "telephoneCountryCode",
                "telephoneNumber",
                errors,
                true
        );

        assertTrue(errors.isEmpty(), "Optional phone should allow both fields empty");
    }

    @Test
    @Disabled
    void invalidCallingCode_adds0701_onCountryCodeField() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        validator.validateMandatoryPhone(
                "+999",// unsupported calling code
                "2070313000",
                "telephoneCountryCode",
                "telephoneNumber",
                errors,
                false
        );

        assertEquals(1, errors.size());
        assertEquals(IntegrationEventError.ERROR_0701, errors.get(0).getError());
        assertEquals("telephoneCountryCode", errors.get(0).getErrorMessage());
    }

    @Test
    @Disabled
    void invalidPhoneNumber_adds0701_onPhoneField() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        validator.validateMandatoryPhone(
                "+44",
                "000", // invalid for UK
                "telephoneCountryCode",
                "telephoneNumber",
                errors,
                false
        );

        assertEquals(1, errors.size());
        assertEquals(IntegrationEventError.ERROR_0701, errors.get(0).getError());
        assertEquals("Invalid Phone Country Code and Phone number combination",
                errors.get(0).getErrorMessage());
    }

}
