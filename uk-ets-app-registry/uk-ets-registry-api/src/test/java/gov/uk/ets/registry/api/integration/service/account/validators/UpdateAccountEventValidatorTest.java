package gov.uk.ets.registry.api.integration.service.account.validators;

import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.validation.AccountNotFoundValidationException;
import gov.uk.ets.registry.api.account.validation.AccountStatusValidationException;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.common.CountryMap;
import gov.uk.ets.registry.api.integration.consumer.ValidatorOperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.List;
import java.util.Set;

import static gov.uk.ets.registry.api.account.domain.types.AccountHolderType.INDIVIDUAL;
import static gov.uk.ets.registry.api.transaction.domain.type.AccountType.OPERATOR_HOLDING_ACCOUNT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UpdateAccountEventValidator}.
 */
@ExtendWith(MockitoExtension.class)
class UpdateAccountEventValidatorTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AccountOperatorUpdateService accountOperatorUpdateService;

    @Mock
    private CompliantEntityRepository compliantEntityRepository;

    @Mock
    private CountryMap countryMap;

    private CommonAccountValidator commonValidator;
    private UpdateAccountEventValidator validator;

    @BeforeEach
    void setUp() throws Exception {
        commonValidator = new CommonAccountValidator(
                ValidatorOperationType.UPDATE_ACCOUNT,
                countryMap
        );

        validator = new UpdateAccountEventValidator(
                accountService,
                accountOperatorUpdateService,
                new UpdateAccountFormatValidator(),
                compliantEntityRepository,
                commonValidator
        );

        var field = UpdateAccountEventValidator.class.getDeclaredField("maritimeStartingYear");
        field.setAccessible(true);
        field.set(validator, 2026);
    }

    private UpdateAccountDetailsMessage baseMessage() {
        UpdateAccountDetailsMessage msg = new UpdateAccountDetailsMessage();
        msg.setRegistryId("10000055");
        msg.setPermitId("PERMIT-1");
        msg.setInstallationName("Test installation");
        msg.setAccountType("OPERATOR_HOLDING_ACCOUNT");
        msg.setFirstYearOfVerifiedEmissions(2024);
        msg.setInstallationActivityTypes(Set.of("COMBUSTION_PLANTS"));

        // Required for aircraft/maritime tests
        msg.setMonitoringPlanId("MP-123");
        msg.setCompanyImoNumber("IMO-12345");

        return msg;
    }

    private AccountHolderMessage validHolder() {
        AccountHolderMessage h = new AccountHolderMessage();
        h.setAccountHolderType(INDIVIDUAL.name());
        h.setName("Test Holder");
        h.setAddressLine1("Line 1");
        h.setTownOrCity("London");
        h.setCountry("GB");
        h.setPostalCode("AB12 3CD");
        return h;
    }

    private AccountUpdatingEvent event() {
        AccountUpdatingEvent ev = new AccountUpdatingEvent();
        ev.setAccountDetails(baseMessage());
        ev.setAccountHolder(validHolder());
        return ev;
    }

    @Test
    void shouldAddError_whenAccountTypeMissing() {
        String ref = "Account Type";
        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType(null); // missing

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                String.format("Expected ERROR_0303 when %s is missing", ref)
        );
    }

    @Test
    void shouldAddError_whenAccountTypeInvalidFormat() {
        String ref = "Account Type";
        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType(OPERATOR_HOLDING_ACCOUNT.name() + "1515");
        // invalid format, not missing

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                String.format("Expected ERROR_0301 for invalid format of %s", ref)
        );
    }

    @Test
    void shouldAddError_whenAccountTypeInvalidFormat2() {
        String ref = "Account Type";
        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("%%%"); // invalid format

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when Account Type format is invalid"
        );
    }

    @Test
    void shouldAddError_whenAccountTypeNotOneOfAcceptedValues() {
        String ref = "Account Type";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("UNKNOWN_TYPE"); // not accepted

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0316, ref),
                String.format("Expected ERROR_0316 for unrecognised %s", ref)
        );
    }

    @Test
    void shouldNotAddError_whenAccountTypeValid() {
        String ref = "Account Type";
        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0303, ref)
                        && notContainsError(errors, IntegrationEventError.ERROR_0301, ref)
                        && notContainsError(errors, IntegrationEventError.ERROR_0316, ref),
                "Expected no errors for valid Service = OPERATOR_HOLDING_ACCOUNT"
        );
    }

    // -------------------------------------------------------------------------
    @Test
    void shouldAddError_whenRegistryIdNotNumeric() {
        String ref = "Registry ID";
        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setRegistryId("ABC");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                String.format("Expected ERROR_0301 data format exception for field %s", ref)
        );
    }

    @Test
    void shouldAddError_whenRegistryIdMissing() {
        String ref = "Registry ID";
        AccountUpdatingEvent ev2 = event();
        ev2.getAccountDetails().setRegistryId(null);

        List<IntegrationEventErrorDetails> errors2 =
                validator.validate(ev2, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors2, IntegrationEventError.ERROR_0303, ref),
                String.format("Expected ERROR_0303 data missing mandatory field %s", ref)
        );
    }

    @Test
    void shouldAddError_whenRegistryStatusInvalid() throws Exception {
        String ref = "Account status";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setRegistryId("123");

        when(accountService.validateAccountUpdateRules(
                eq(ev.getAccountHolder().getAccountHolderType()),
                eq(OperatorType.INSTALLATION),
                eq(123L)
        )).thenThrow(new AccountStatusValidationException("Invalid status"));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0313, ref),
                "Expected ERROR_0313 when AccountStatusValidationException is thrown"
        );
    }




    @Test
    void shouldAddError_whenRegulatedActivitiesMissingForInstallation() {
        String ref = "Regulated activities";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setInstallationActivityTypes(null); // missing

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when regulated activities are missing for Installation"
        );
    }

    @Test
    void shouldAddError_whenRegulatedActivitiesInvalid() {
        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setInstallationActivityTypes(Set.of("INVALID"));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                errors.stream().anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0312),
                "Expected ERROR_0312 when regulated activities contain invalid values"
        );
    }

    @Test
    void shouldNotAddError_whenValidActivities() {
        String ref = "Regulated activities";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setInstallationActivityTypes(Set.of(InstallationActivityType.PRODUCTION_OF_COKE.name())); // valid

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0303, ref)
                        && notContainsError(errors, IntegrationEventError.ERROR_0312, ref),
                "Expected no errors for valid regulated activities"
        );
    }


    //Installation name
    @Test
    void shouldAddError_whenInstallationNameMissing() {
        String ref = "Installation Name";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setInstallationName(null);
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when Installation Name is missing"
        );
    }

    @Test
    void shouldAddError_whenInstallationNameTooLong() {
        String ref = "Installation Name";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");

        // Create a very long name (300 chars)
        ev.getAccountDetails().setInstallationName("A".repeat(300));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when Installation Name exceeds max length"
        );
    }

    @Test
    void shouldNotAddError_whenInstallationNameValid() {
        String ref = "Installation Name";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");
        ev.getAccountDetails().setInstallationName("Valid Installation");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0303, ref)
                        && notContainsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected no errors for valid installation name"
        );
    }

    //Legal status cases

    @Test
    void shouldAddError_whenLegalStatusChanged() throws Exception {
        String ref = "Operator type";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setRegistryId("123");

        when(accountService.validateAccountUpdateRules(
                eq(ev.getAccountHolder().getAccountHolderType()),
                eq(OperatorType.MARITIME_OPERATOR),
                eq(123L)
        )).thenReturn(false); // changed

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0314, ref),
                "Expected ERROR_0314 when Operator Type is changed"
        );
    }


    @Test
    void shouldNotAddError_whenLegalStatusSame() throws Exception {
        String ref = "Operator type";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setRegistryId("5555");

        when(accountService.validateAccountUpdateRules(
                eq(ev.getAccountHolder().getAccountHolderType()),
                eq(OperatorType.INSTALLATION),
                eq(5555L)
        )).thenReturn(true); // SAME

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0314, ref),
                "Expected no ERROR_0314 when Operator Type unchanged"
        );
    }


    @Test
    void shouldAddError_whenAccountStatusInvalid() throws Exception {
        String ref = "Account status";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setRegistryId("10");

        when(accountService.validateAccountUpdateRules(
                eq(ev.getAccountHolder().getAccountHolderType()),
                eq(OperatorType.INSTALLATION),
                eq(10L)
        )).thenThrow(new AccountStatusValidationException("Closed"));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0313, ref),
                "Expected ERROR_0313 for invalid account status"
        );
    }


    @Test
    void shouldAddError_whenAccountNotFound() throws Exception {
        String ref = "Registry ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setRegistryId("10");

        when(accountService.validateAccountUpdateRules(
                eq(ev.getAccountHolder().getAccountHolderType()),
                eq(OperatorType.INSTALLATION),
                eq(10L)
        )).thenThrow(new AccountNotFoundValidationException("Not found"));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0317, ref),
                "Expected ERROR_0317 when account not found"
        );
    }

    //Permit ID

    @Test
    void shouldAddError_whenPermitIdMissingForInstallation() {
        String ref = "Permit ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");
        ev.getAccountDetails().setPermitId(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when Permit ID is missing for Installation"
        );
    }

    @Test
    void shouldAddError_whenPermitIdInvalidFormat() {
        String ref = "Permit ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");
        ev.getAccountDetails().setPermitId("1".repeat(259)); // invalid format

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when Permit ID format is invalid"
        );
    }

    @Test
    void shouldAddError_whenPermitIdNotUnique() {
        String ref = "Permit ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");
        ev.getAccountDetails().setPermitId("PERMIT-123");
        ev.getAccountDetails().setRegistryId("999");

        // The validator will call: validateInstallationPermitUniqueness("PERMIT-123", 999L)
        when(accountService.validateInstallationPermitUniqueness(eq("PERMIT-123"), eq(999L)))
                .thenReturn(false); // NOT UNIQUE

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0318, ref),
                "Expected ERROR_0318 when Permit ID already exists"
        );
    }

    @Test
    void shouldNotAddError_whenPermitIdUnique() {
        String ref = "Permit ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setAccountType("OPERATOR_HOLDING_ACCOUNT");
        ev.getAccountDetails().setPermitId("PERMIT-ABC");
        ev.getAccountDetails().setRegistryId("777");

        when(accountService.validateInstallationPermitUniqueness(eq("PERMIT-ABC"), eq(777L)))
                .thenReturn(true); // UNIQUE

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0318, ref),
                "Expected NO 0318 error when Permit ID is unique"
        );
    }

    @Test
    void shouldAddError_whenMonitoringPlanMissingForAircraft() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.AIRCRAFT_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when Monitoring Plan ID is missing for Aircraft Operator"
        );
    }

    @Test
    void shouldAddError_whenMonitoringPlanInvalidFormat() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId("1".repeat(259)); // invalid

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.AIRCRAFT_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when Monitoring Plan ID has invalid format"
        );
    }

    @Test
    void shouldAddError_whenMonitoringPlanNotUnique() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId("MP-XYZ");
        ev.getAccountDetails().setRegistryId("555");

        when(accountService.validateAircraftUniquenessMonitoringPlanId(eq("MP-XYZ"), eq(555L)))
                .thenReturn(false); // NOT UNIQUE

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.AIRCRAFT_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0319, ref),
                "Expected ERROR_0319 when Monitoring Plan ID already exists"
        );
    }

    @Test
    void shouldNotAddError_whenMonitoringPlanUnique() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId("MP-ABC");
        ev.getAccountDetails().setRegistryId("999");

        when(accountService.validateAircraftUniquenessMonitoringPlanId(eq("MP-ABC"), eq(999L)))
                .thenReturn(true); // UNIQUE

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.AIRCRAFT_OPERATOR);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0319, ref),
                "Expected no 0319 error when Monitoring Plan is unique"
        );
    }

    @Test
    void shouldAddError_whenMonitoringPlanMissingForMaritime() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when Monitoring Plan ID is missing for Maritime"
        );
    }

    @Test
    void shouldAddError_whenMonitoringPlanInvalidFormatForMaritime() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId("1".repeat(259));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 for invalid Monitoring Plan ID format (Maritime)"
        );
    }

    @Test
    void shouldAddError_whenMonitoringPlanNotUniqueForMaritime() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId("MP-MAR-001");
        ev.getAccountDetails().setRegistryId("777");

        when(accountService.validateMaritimeUniquenessMonitoringPlanId(eq("MP-MAR-001"), eq(777L)))
                .thenReturn(false); // NOT unique

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0319, ref),
                "Expected ERROR_0319 when Maritime Monitoring Plan is not unique"
        );
    }

    @Test
    void shouldNotAddError_whenMonitoringPlanUniqueForMaritime() {
        String ref = "Monitoring Plan ID";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setMonitoringPlanId("MP-MAR-002");
        ev.getAccountDetails().setRegistryId("888");

        when(accountService.validateMaritimeUniquenessMonitoringPlanId(eq("MP-MAR-002"), eq(888L)))
                .thenReturn(true); // UNIQUE

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0319, ref),
                "Expected no ERROR_0319 when Maritime Monitoring Plan is unique"
        );
    }

    @Test
    void shouldAddError_whenImoMissingForMaritime() {
        String ref = "Company IMO number";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setCompanyImoNumber(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when IMO number is missing for Maritime"
        );
    }

    @Test
    void shouldAddError_whenImoInvalidFormatForMaritime() {
        String ref = "Company IMO number";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setCompanyImoNumber("1".repeat(259)); // invalid

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when IMO format is invalid"
        );
    }

    @Test
    void shouldAddError_whenImoNotUniqueForMaritime() {
        String ref = "Company IMO number";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setCompanyImoNumber("IMO-12345");
        ev.getAccountDetails().setRegistryId("999");

        when(accountService.validateMaritimeUniquenessImo(eq("IMO-12345"), eq(999L)))
                .thenReturn(false); // NOT unique

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0310, ref),
                "Expected ERROR_0310 when IMO is not unique"
        );
    }

    @Test
    void shouldNotAddError_whenImoUniqueForMaritime() {
        String ref = "Company IMO number";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setCompanyImoNumber("IMO-54321");
        ev.getAccountDetails().setRegistryId("1001");

        when(accountService.validateMaritimeUniquenessImo(eq("IMO-54321"), eq(1001L)))
                .thenReturn(true); // UNIQUE

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.MARITIME_OPERATOR);

        assertTrue(
                notContainsError(errors, IntegrationEventError.ERROR_0310, ref),
                "Expected no ERROR_0310 when IMO is unique"
        );
    }

    @Test
    void shouldAddError_whenIndividualFirstNameMissing() {
        String ref = "Account Holder Name";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("INDIVIDUAL");
        ev.getAccountHolder().setName(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when Account Holder Name is missing"
        );
    }

    @Test
    void shouldAddError_whenOrganisationNameMissing() {
        String ref = "Account Holder Name";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("ORGANISATION");
        ev.getAccountHolder().setName(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when Organisation Name is missing"
        );
    }

    @Test
    void shouldAddError_whenNoCRNFlagMissing() {
        String ref = "Registration Number Not Exist";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("ORGANISATION");
        ev.getAccountHolder().setCrnNotExist(null); // missing flag

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when CRN flag is missing"
        );
    }

    @Test
    void shouldAddError_whenCRNMissingButFlagFalse() {
        String ref = "CRN";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("ORGANISATION");
        ev.getAccountHolder().setCrnNotExist(false);
        ev.getAccountHolder().setCompanyRegistrationNumber(null); // missing

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0304, ref),
                "Expected ERROR_0304 when CRN missing but flag=false"
        );
    }

    @Test
    void shouldAddError_whenCRNJustificationMissingButFlagTrue() {
        String ref = "CRN";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("ORGANISATION");
        ev.getAccountHolder().setCrnNotExist(true);
        ev.getAccountHolder().setCrnJustification(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0304, ref),
                "Expected ERROR_0304 when justification missing but flag=true"
        );
    }

    @Test
    void shouldAddError_whenAddressLine1Missing() {
        String ref = "Address Line 1";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAddressLine1(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 for missing Address Line 1"
        );
    }

    @Test
    void shouldAddError_whenTownOrCityMissing() {
        String ref = "Town Or City";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setTownOrCity(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 for missing Town/City"
        );
    }

    @Test
    void shouldAddError_whenCountryMissing() {
        String ref = "Country";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setCountry(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 for missing Country"
        );
    }


    @Test
    void shouldAddError_whenCountryInvalid() {
        String ref = "Country";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setCountry("XX");
        when(countryMap.containsCountryCode("XX")).thenReturn(false);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0307, ref),
                "Expected ERROR_0307 when Country is invalid"
        );
    }

    @Test
    void shouldAddError_whenCountryIsInvalid() {
        String ref = "Country";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setCountry("123");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 invalid country format"
        );
    }

    @Test
    void shouldAddError_whenPostalCodeInvalidFormatForUK() {
        String ref = "Postal Code";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setCountry("UK");
        ev.getAccountHolder().setPostalCode("1".repeat(259)); // invalid format

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when Postal Code format is invalid"
        );
    }

    @Test
    void shouldAddError_whenFYVEMissing() {
        String ref = "First Year Of Verified Emission";

        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setFirstYearOfVerifiedEmissions(null); // missing

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when FYVE is missing"
        );
    }

    @Test
    void shouldAddError_whenAccountHolderTypeMissing() {
        String ref = "Account Holder Type";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, ref),
                "Expected ERROR_0303 when Account Holder Type is missing"
        );
    }

    @Test
    void shouldAddError_whenAccountHolderTypeInvalid() {
        String ref = "Account Holder Type";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("ALIEN"); // invalid type

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0309, ref),
                "Expected ERROR_0309 for invalid Account Holder Type"
        );
    }

    @Test
    void shouldAddError_whenAddressLine1InvalidFormat() {
        String ref = "Address Line 1";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAddressLine1("1".repeat(259)); // invalid

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when Address Line 1 has invalid format"
        );
    }

    @Test
    void shouldAddError_whenTownOrCityInvalidFormat() {
        String ref = "Town Or City";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setTownOrCity("*".repeat(259)); // invalid

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 when Town/City has invalid format"
        );
    }

    @Test
    void shouldAddError_whenCRNInvalidFormat() {
        String ref = "CRN";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("ORGANISATION");
        ev.getAccountHolder().setCrnNotExist(false);
        ev.getAccountHolder().setCompanyRegistrationNumber("1".repeat(500)); // invalid

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 for invalid CRN format"
        );
    }

    @Test
    void shouldAddError_whenCRNJustificationInvalidFormat() {
        String ref = "CRN";

        AccountUpdatingEvent ev = event();
        ev.getAccountHolder().setAccountHolderType("ORGANISATION");
        ev.getAccountHolder().setCrnNotExist(true);
        ev.getAccountHolder().setCrnJustification("1".repeat(5000)); // invalid

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0301, ref),
                "Expected ERROR_0301 for invalid CRN justification format"
        );
    }

    @Test
    void shouldAddError_whenAccountDetailsMissing() {
        AccountUpdatingEvent ev = new AccountUpdatingEvent();
        ev.setAccountDetails(null);
        ev.setAccountHolder(new AccountHolderMessage());

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, "Account Details"),
                "Expected ERROR_0303 when Account Details is missing"
        );
    }

    @Test
    void shouldAddError_whenAccountHolderMissing() {
        AccountUpdatingEvent ev = new AccountUpdatingEvent();
        ev.setAccountDetails(new UpdateAccountDetailsMessage());
        ev.setAccountHolder(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, "Account Holder"),
                "Expected ERROR_0303 when Account Holder is missing"
        );
    }

    @Test
    void shouldAddError_forRegulatorNotValidField() {
        AccountUpdatingEvent ev = event();
        ev.getAccountDetails().setRegulator(null);

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors, IntegrationEventError.ERROR_0303, "Regulator"),
                "Expected ERROR_0303 when Regulator is missing"
        );

        AccountUpdatingEvent ev1 = event();
        ev1.getAccountDetails().setRegulator("OTHER_ONE");

        List<IntegrationEventErrorDetails> errors1 =
                validator.validate(ev1, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors1, IntegrationEventError.ERROR_0308, "Regulator"),
                "Expected ERROR_0308 when Regulator is not accepted value"
        );

        AccountUpdatingEvent ev2 = event();
        ev2.getAccountDetails().setRegulator("OTHER_ONE");

        List<IntegrationEventErrorDetails> errors2 =
                validator.validate(ev2, OperatorType.INSTALLATION);

        assertTrue(
                containsError(errors1, IntegrationEventError.ERROR_0308, "Regulator"),
                "Expected ERROR_0308 when Regulator is not accepted value"
        );
    }

    private boolean containsError(List<IntegrationEventErrorDetails> errors,
                                  IntegrationEventError expectedError,
                                  String expectedField) {

        return errors.stream().anyMatch(e ->
                e.getError() == expectedError &&
                        e.getErrorMessage() != null &&
                        e.getErrorMessage().startsWith(expectedField)
        );
    }

    private boolean notContainsError(List<IntegrationEventErrorDetails> errors,
                                     IntegrationEventError expectedError,
                                     String expectedField) {

        return errors.stream().noneMatch(e ->
                e.getError() == expectedError &&
                        e.getErrorMessage() != null &&
                        e.getErrorMessage().startsWith(expectedField)
        );
    }

}
