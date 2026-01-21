package gov.uk.ets.registry.api.integration.service.account;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.common.CountryMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import gov.uk.ets.registry.api.integration.service.account.validators.AccountOpeningEventValidator;
import gov.uk.ets.registry.api.integration.consumer.ValidatorOperationType;
import gov.uk.ets.registry.api.integration.service.account.validators.CommonAccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@ExtendWith(MockitoExtension.class)
class AccountEventValidatorTest {

    @Mock
    private CountryMap countryMap;
    @Mock
    private AccountService accountService;

    private AccountOpeningEventValidator validator;

    @BeforeEach
    public void setup() {

        CommonAccountValidator createAccountValidator = new CommonAccountValidator(
                ValidatorOperationType.CREATE_ACCOUNT,
                countryMap
        );

        validator = new AccountOpeningEventValidator(accountService, countryMap, createAccountValidator);
        ReflectionTestUtils.setField(validator, "maritimeStartingYear", 2026);
    }

    @Test
    void testValidateMaritimeAccountOpeningEvent() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("maritime account name");
        accountDetailsMessage.setEmitterId("3214567");
        accountDetailsMessage.setCompanyImoNumber("IMO number");
        accountDetailsMessage.setMonitoringPlanId("Monitoring plan id");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setPostalCode("123456");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("UK")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.MARITIME_OPERATOR);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void testValidateMaritimeAccountOpeningEventWithoutMonitoringPlanId() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("maritime account name");
        accountDetailsMessage.setEmitterId("3214567");
        accountDetailsMessage.setCompanyImoNumber("IMO number");
        accountDetailsMessage.setMonitoringPlanId(null);
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setPostalCode("123456");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("UK")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.MARITIME_OPERATOR);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void testValidateAircraftAccountOpeningEventWithoutMonitoringPlanId() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("Aircraft account name");
        accountDetailsMessage.setEmitterId("3214567");
        accountDetailsMessage.setMonitoringPlanId(null);
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setPostalCode("123456");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("UK")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.AIRCRAFT_OPERATOR);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void testValidateInstallationAccountOpeningEvent() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void testValidateAircraftOperatorWithIndividualAccountHolder() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("aircraft account name");
        accountDetailsMessage.setEmitterId("3214567");
        accountDetailsMessage.setMonitoringPlanId("Monitoring plan id");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("INDIVIDUAL");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("IT");
        accountHolderMessage.setPostalCode("123456");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("IT")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.AIRCRAFT_OPERATOR);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void testValidateEmitterIdAlreadyExists() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(accountService.emitterIdExists("321456")).thenReturn(true);
        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0102);
    }

    @Test
    void testValidatePermitIdAlreadyExists() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(accountService.installationPermitIdExists("321456")).thenReturn(true);
        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0105);
    }

    @Test
    void testValidateInvalidCountry() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("TT");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("TT")).thenReturn(false);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0107);
    }

    @Test
    void testValidateWithoutCrnFlag() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0103);
    }

    @Test
    void testValidateWithoutCrn() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0104);
    }

    @Test
    void testValidateInstallationInvalidFirstYearOfVerifiedEmissions() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(999);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0111);
        assertThat(result.get(1).getError()).isEqualTo(IntegrationEventError.ERROR_0101);
    }

    @Test
    void testValidateMaritimeInvalidDataFormatAndFirstYearOfVerifiedEmissions() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(999);
        accountDetailsMessage.setCompanyImoNumber("Monitoring Plan");
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.MARITIME_OPERATOR);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0106);
        assertThat(result.get(1).getError()).isEqualTo(IntegrationEventError.ERROR_0101);
    }
    
    @Test
    void testValidateMaritimeInvalidFirstYearOfVerifiedEmissions() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2025);
        accountDetailsMessage.setCompanyImoNumber("Monitoring Plan");
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.MARITIME_OPERATOR);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0106);
    }

    @Test
    void testValidateUnknownRegulator() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("UNKNOWN");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0108);
    }

    @Test
    void testValidateUnknownActivityType() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of("UNKNOWN"));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0112);
    }

    @Test
    void testValidateUnknownAccountHolderType() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("unknown");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("GR")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0109);
    }

    @Test
    void testValidatePostalCode() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("321456");
        accountDetailsMessage.setPermitId("321456");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("UK")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    void testValidateFieldsLength() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId(getString(999));
        accountDetailsMessage.setPermitId(getString(999));
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName(getString(999));
        accountHolderMessage.setAddressLine1(getString(999));
        accountHolderMessage.setAddressLine2(getString(999));
        accountHolderMessage.setTownOrCity(getString(999));
        accountHolderMessage.setStateOrProvince(getString(999));
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setPostalCode(getString(99));
        accountHolderMessage.setCrnNotExist(true);
        accountHolderMessage.setCrnJustification(getString(9999));
        event.setAccountHolder(accountHolderMessage);

        Mockito.when(countryMap.containsCountryCode("UK")).thenReturn(true);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(9);
        assertThat(result.stream().map(IntegrationEventErrorDetails::getError).collect(Collectors.toSet()))
            .containsOnly(IntegrationEventError.ERROR_0101);
    }

    @Test
    void testValidateWithEmptyPayload() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        event.setAccountDetails(new AccountDetailsMessage());
        event.setAccountHolder(new AccountHolderMessage());

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event, OperatorType.INSTALLATION);

        // then
        assertThat(result).hasSize(12);
        assertThat(result.stream().map(IntegrationEventErrorDetails::getError).collect(Collectors.toSet()))
            .containsOnly(IntegrationEventError.ERROR_0103);
    }

    private String getString(int length) {
        return IntStream.range(0, length).boxed().map(Object::toString).reduce("", (i1, i2) -> i1 + "a");
    }
}
