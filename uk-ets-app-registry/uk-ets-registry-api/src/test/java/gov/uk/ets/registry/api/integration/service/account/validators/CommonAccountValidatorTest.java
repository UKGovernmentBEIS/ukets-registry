package gov.uk.ets.registry.api.integration.service.account.validators;

import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.common.CountryMap;
import gov.uk.ets.registry.api.integration.consumer.ValidatorOperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommonAccountValidatorTest {

    private CountryMap countryMap;
    private AccountService accountService;
    private CommonAccountValidator validator;

    @BeforeEach
    void setUp() {
        countryMap = mock(CountryMap.class);
        accountService = mock(AccountService.class);
        validator = new CommonAccountValidator(ValidatorOperationType.CREATE_ACCOUNT, countryMap);
    }

    @Test
    void shouldReturnTrueWhenStringIsNullOrEmpty() {
        assertTrue(validator.isNullOrEmptyString(null));
        assertTrue(validator.isNullOrEmptyString(""));
        assertFalse(validator.isNullOrEmptyString("abc"));
    }

    @Test
    void shouldAddErrorWhenStringExceedsMaxLength() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();
        String longString = "x".repeat(300);

        validator.validateLength(longString, "Field", 256, errors);

        assertEquals(1, errors.size());
        assertEquals(IntegrationEventError.ERROR_0101, errors.get(0).getError());
        assertEquals("Field", errors.get(0).getErrorMessage());
    }

    @Test
    void shouldAddErrorWhenMandatoryFieldIsNull() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        validator.validateMandatoryField(null, "Test Field", errors);

        assertEquals(1, errors.size());
        assertEquals(IntegrationEventError.ERROR_0103, errors.get(0).getError());
        assertEquals("Test Field", errors.get(0).getErrorMessage());
    }

    @Test
    void shouldAddErrorWhenBusinessPredicateFails() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        validator.validateMandatoryField(
                "INVALID_TYPE",
                "Account Holder Type",
                value -> true, // always fails business rule
                IntegrationEventError.ERROR_0109,
                errors
        );

        assertEquals(1, errors.size());
        assertEquals(IntegrationEventError.ERROR_0109, errors.get(0).getError());
    }

    @Test
    void shouldAddErrorWhenCountryIsInvalid() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        when(countryMap.containsCountryCode("XX")).thenReturn(false);

        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setName("John Doe");
        holder.setAddressLine1("123 Street");
        holder.setTownOrCity("London");
        holder.setCountry("XX");
        holder.setAccountHolderType("INDIVIDUAL");

        validator.validateAccountHolder(holder, errors);

        assertTrue(errors.stream().anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0107));
    }

    @Test
    void shouldPassValidationForValidCountry() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        when(countryMap.containsCountryCode("GB")).thenReturn(true);

        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setName("John Doe");
        holder.setAddressLine1("123 Street");
        holder.setTownOrCity("London");
        holder.setCountry("GB");
        holder.setAccountHolderType("INDIVIDUAL");

        validator.validateAccountHolder(holder, errors);

        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldAddErrorWhenCrnJustificationIsMissing() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setName("Company Ltd");
        holder.setAddressLine1("1 Road");
        holder.setTownOrCity("Athens");
        holder.setCountry("GR");
        holder.setAccountHolderType("ORGANISATION");
        holder.setCrnNotExist(true);
        holder.setCrnJustification("");

        when(countryMap.containsCountryCode("GR")).thenReturn(true);

        validator.validateAccountHolder(holder, errors);

        assertTrue(errors.stream().anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0104));
    }

    @Test
    void shouldAddErrorWhenCompanyRegistrationNumberMissing() {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setName("Company Ltd");
        holder.setAddressLine1("1 Road");
        holder.setTownOrCity("Athens");
        holder.setCountry("GR");
        holder.setAccountHolderType("ORGANISATION");
        holder.setCrnNotExist(false);
        holder.setCompanyRegistrationNumber("");

        when(countryMap.containsCountryCode("GR")).thenReturn(true);

        validator.validateAccountHolder(holder, errors);

        assertTrue(errors.stream().anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0104));
    }

    @Test
    void shouldRemapErrorCodesForUpdateOperation() {
        CommonAccountValidator updateValidator =
                new CommonAccountValidator(ValidatorOperationType.UPDATE_ACCOUNT, countryMap);

        var field = updateValidator.getClass();
        assertEquals(
                IntegrationEventError.ERROR_0307,
                updateValidatorTestHelper(updateValidator, IntegrationEventError.ERROR_0107)
        );
    }

    // helper method to call private resolveError()
    private IntegrationEventError updateValidatorTestHelper(CommonAccountValidator validator, IntegrationEventError baseError) {
        try {
            var method = CommonAccountValidator.class.getDeclaredMethod("resolveError", IntegrationEventError.class);
            method.setAccessible(true);
            return (IntegrationEventError) method.invoke(validator, baseError);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
