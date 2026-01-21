package gov.uk.ets.registry.api.integration.service.metscontacts;


import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.validation.AccountHolderTypeMissmatchException;
import gov.uk.ets.registry.api.account.validation.AccountNotFoundValidationException;
import gov.uk.ets.registry.api.account.validation.AccountStatusValidationException;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import gov.uk.ets.registry.api.common.CountryMap;
import gov.uk.ets.registry.api.integration.service.account.validators.CommonAccountValidator;
import gov.uk.ets.registry.api.integration.consumer.ValidatorOperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MetsContactsEventValidatorTest {
    private static final gov.uk.ets.registry.api.account.web.model.OperatorType OPERATOR_TYPE =
            gov.uk.ets.registry.api.account.web.model.OperatorType.INSTALLATION;

    private CommonAccountValidator commonValidator;
    private MetsContactsEventValidator validator;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        commonValidator = new CommonAccountValidator(ValidatorOperationType.METS_CONTACTS, mock(CountryMap.class));
        validator = new MetsContactsEventValidator(commonValidator, accountService);
    }

    private MetsContactsMessage validMessage() {
        MetsContactsMessage msg = new MetsContactsMessage();
        msg.setFirstName("John");
        msg.setLastName("Doe");
        msg.setTelephoneCountryCode("UK");
        msg.setTelephoneNumber("123456");
        msg.setMobilePhoneCountryCode("UK");
        msg.setMobileNumber("987654");
        msg.setEmail("john@doe.com");
        msg.setUserType(OperatorType.OPERATOR_ADMIN.name());
        msg.setContactTypes(List.of(MetsAccountContactType.PRIMARY.name()));
        return msg;
    }

    private MetsContactsEvent buildEvent(MetsContactsMessage msg) {
        MetsContactsEvent event = new MetsContactsEvent();
        event.setOperatorId("123456");
        event.setDetails(List.of(msg));
        return event;
    }

    // ---------------------------------------------------------------------
    // Mandatory fields
    // ---------------------------------------------------------------------

    @Test
    void testFirstNameMandatoryAndCharsLimit() throws AccountStatusValidationException {
        String ref = "First name";
        MetsContactsMessage msg = validMessage();
        msg.setFirstName(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0702, ref),
                String.format("Expected ERROR_0702 for field '%s'",ref)
        );

        MetsContactsMessage msg2 = validMessage();
        msg2.setFirstName("A".repeat(300));
        List<IntegrationEventErrorDetails> errors2 =
                validator.validate(buildEvent(msg2), OPERATOR_TYPE);
        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0701, ref),
                String.format("Expected ERROR_0701 exited chars limit 256 for field '%s'", ref)
        );
    }

    @Test
    void testLastNameMandatoryAndCharsLimit() throws AccountStatusValidationException {
        String ref = "Last name";
        MetsContactsMessage msg = validMessage();
        msg.setLastName(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0702, ref),
                String.format("Expected ERROR_0702 for field '%s'",ref)
        );

        MetsContactsMessage msg2 = validMessage();
        msg2.setLastName("A".repeat(300));
        List<IntegrationEventErrorDetails> errors2 =
                validator.validate(buildEvent(msg2), OPERATOR_TYPE);
        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0701, ref),
                String.format("Expected ERROR_0701 exited chars limit 256 for field '%s'", ref)
        );
    }

    @Test
    void testMissingTelephoneCountryCode() throws AccountStatusValidationException {
        String ref = "Telephone country code";
        MetsContactsMessage msg = validMessage();
        msg.setTelephoneCountryCode(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0702, ref),
                String.format("Expected ERROR_0702 for field '%s'",ref)
        );
    }

    @Test
    void testTelephoneNumberMandatoryAndInvalidCharsAndCharsLimit() throws AccountStatusValidationException {
        String ref = "Telephone number";
        MetsContactsMessage msg = validMessage();
        msg.setTelephoneNumber(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0702, ref),
                String.format("Expected ERROR_0702 for field '%s'",ref)
        );

        MetsContactsMessage msg2 = validMessage();
        msg2.setTelephoneNumber("325AS15448445");
        List<IntegrationEventErrorDetails> errors2 =
                validator.validate(buildEvent(msg2), OPERATOR_TYPE);
        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0701, ref),
                String.format("Expected ERROR_0701 invalid chars for field '%s'", ref)
        );

        MetsContactsMessage msg3 = validMessage();
        msg3.setTelephoneNumber("514845514845514845");
        List<IntegrationEventErrorDetails> errors3 =
                validator.validate(buildEvent(msg3), OPERATOR_TYPE);
        assertTrue(
                containsError(errors3, IntegrationEventError.ERROR_0701, ref),
                String.format("Expected ERROR_0701 invalid number length '%s'", ref)
        );
    }

    @Test
    void testCountryTelephoneCodeMandatoryAndInvalidCharsAndCharsLimit() throws AccountStatusValidationException {
        String ref = "Telephone country code";
        MetsContactsMessage msg = validMessage();
        msg.setTelephoneCountryCode(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0702, ref),
                String.format("Expected ERROR_0702 for field '%s'",ref)
        );

        MetsContactsMessage msg2 = validMessage();
        msg2.setTelephoneCountryCode("3A2");
        List<IntegrationEventErrorDetails> errors2 =
                validator.validate(buildEvent(msg2), OPERATOR_TYPE);
        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0701, ref),
                String.format("Expected ERROR_0701 invalid chars for field '%s'", ref)
        );

        MetsContactsMessage msg3 = validMessage();
        msg3.setTelephoneCountryCode("256984");
        List<IntegrationEventErrorDetails> errors3 =
                validator.validate(buildEvent(msg3), OPERATOR_TYPE);
        assertTrue(
                containsError(errors3, IntegrationEventError.ERROR_0701, ref),
                String.format("Expected ERROR_0701 invalid number length '%s'", ref)
        );
    }

    @Test
    void testMobilePhoneNumberOptionalButIfPresentMustBeValid() throws AccountStatusValidationException {
        String ref = "Mobile number";

        MetsContactsMessage msg = validMessage();
        msg.setMobileNumber(null);
        msg.setMobilePhoneCountryCode(null);
        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertFalse(
                containsError(errors, IntegrationEventError.ERROR_0701, ref),
                "Mobile number is optional when country code is also missing"
        );

        MetsContactsMessage msg2 = validMessage();
        msg2.setMobileNumber("325AS15448445");
        msg2.setMobilePhoneCountryCode("44");
        List<IntegrationEventErrorDetails> errors2 =
                validator.validate(buildEvent(msg2), OPERATOR_TYPE);

        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0701, ref),
                "Expected ERROR_0701 invalid chars"
        );

        MetsContactsMessage msg3 = validMessage();
        msg3.setMobileNumber("514845514845514845");
        msg3.setMobilePhoneCountryCode("44");
        List<IntegrationEventErrorDetails> errors3 =
                validator.validate(buildEvent(msg3), OPERATOR_TYPE);

        assertTrue(
                containsError(errors3, IntegrationEventError.ERROR_0701, ref),
                "Expected ERROR_0701 number length too long"
        );
    }

    @Test
    void testMobilePhoneCountryCodeRules() throws AccountStatusValidationException {
        String ref = "Mobile phone";

        // 1. invalid case: Country code exists but mobile number missing
        MetsContactsMessage msg = validMessage();
        msg.setMobileNumber(null);
        msg.setMobilePhoneCountryCode("44");
        List<IntegrationEventErrorDetails> errors = validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0701, ref),
                "Country code cannot exist without mobile number"
        );

        MetsContactsMessage msg2 = validMessage();
        msg2.setMobileNumber("1234567");
        msg2.setMobilePhoneCountryCode("3A2");
        List<IntegrationEventErrorDetails> errors2 = validator.validate(buildEvent(msg2), OPERATOR_TYPE);
        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0701, "Mobile phone country code"),
                "Expected ERROR_0701 invalid mobile country code chars"
        );

        MetsContactsMessage msg3 = validMessage();
        msg3.setMobileNumber("1234567");
        msg3.setMobilePhoneCountryCode("256984");
        List<IntegrationEventErrorDetails> errors3 = validator.validate(buildEvent(msg3), OPERATOR_TYPE);
        assertTrue(
                containsError(errors3, IntegrationEventError.ERROR_0701, "Mobile phone country code"),
                "Expected ERROR_0701 country code too long"
        );
    }

    @Test
    void testUserTypeRules() throws AccountStatusValidationException {
        String ref = "User type";

        MetsContactsMessage msg2 = validMessage();
        msg2.setUserType("1234567");
        List<IntegrationEventErrorDetails> errors2 = validator.validate(buildEvent(msg2), OPERATOR_TYPE);
        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0701, ref),
                "Expected ERROR_0701 for invalid User type"
        );

        MetsContactsMessage msg3 = validMessage();
        msg3.setUserType(null);
        List<IntegrationEventErrorDetails> errors3 = validator.validate(buildEvent(msg3), OPERATOR_TYPE);
        assertTrue(
                containsError(errors3, IntegrationEventError.ERROR_0702, ref),
                "Expected ERROR_0702 missing mandatory field"
        );
    }

    @Test
    void testContactTypeRules() throws AccountStatusValidationException {
        String ref = "Contact type";

        MetsContactsMessage msg = validMessage();
        msg.setContactTypes(List.of("TEST"));
        List<IntegrationEventErrorDetails> errors = validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0701, ref),
                "Expected ERROR_0701 for invalid Contact type"
        );
    }

    @Test
    void bug_1_contactTypesInvalidFormat_shouldReturn0701_not0702() throws AccountStatusValidationException {
        String ref = "Contact type";

        MetsContactsMessage msg = validMessage();
        msg.setContactTypes(List.of("SECO1NDARY"));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0701, ref),
                "Expected ERROR_0701 (Data not in expected format) for invalid Contact type value"
        );

        assertFalse(
                containsError(errors, IntegrationEventError.ERROR_0702, ref),
                "Did not expect ERROR_0702 (Mandatory field is not provided) for invalid Contact type value"
        );
    }

    @Test
    void bug_2_userTypeEmpty_shouldReturn0702_not0701() throws AccountStatusValidationException {
        String ref = "User type";

        MetsContactsMessage msg = validMessage();
        msg.setUserType("");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(buildEvent(msg), OPERATOR_TYPE);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0702, ref),
                "Expected ERROR_0702 (Mandatory field is not provided) for empty User type"
        );

        assertFalse(
                containsError(errors, IntegrationEventError.ERROR_0701, ref),
                "Did not expect ERROR_0701 (Data not in expected format) for empty User type"
        );
    }

    private boolean containsError(List<IntegrationEventErrorDetails> errors,
                                  IntegrationEventError expectedError,
                                  String expectedField) {

        return errors.stream().anyMatch(e ->
                e.getError() == expectedError &&
                        e.getErrorMessage() != null &&
                        e.getErrorMessage().equals(expectedField)
        );
    }

}
