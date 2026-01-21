package gov.uk.ets.registry.api.integration.service.account.validators;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.common.CountryMap;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import static org.hibernate.validator.internal.util.StringHelper.isNullOrEmptyString;

@Component
@RequiredArgsConstructor
public class AccountOpeningEventValidator {

    @Value("${emissions-maritime-starting-year}")
    private Integer maritimeStartingYear;

    private final AccountService accountService;

    private final CountryMap countryMap;


    @Qualifier("createAccountValidator")
    private final CommonAccountValidator createAccountValidator;

    public List<IntegrationEventErrorDetails> validate(AccountOpeningEvent event, OperatorType operatorType) {

        List<IntegrationEventErrorDetails> errorDetails = new ArrayList<>();
        AccountDetailsMessage accountDetails = event.getAccountDetails();

        createAccountValidator.validateMandatoryField(accountDetails.getEmitterId(), "Emitter ID", 255,
                accountService::emitterIdExists, IntegrationEventError.ERROR_0102, errorDetails);

        if (OperatorType.INSTALLATION == operatorType) {
            createAccountValidator.validateMandatoryField(accountDetails.getPermitId(), "Permit ID", 256,
                    accountService::installationPermitIdExists, IntegrationEventError.ERROR_0105, errorDetails);

            createAccountValidator.validateMandatoryField(accountDetails.getInstallationName(), "Installation Name", 256, errorDetails);

            Set<String> installationActivityTypes = accountDetails.getInstallationActivityTypes();
            if (installationActivityTypes == null || installationActivityTypes.isEmpty()) {
                errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0103, "Installation Activity Types"));
            } else if (!Arrays.stream(InstallationActivityType.values()).map(Enum::name).collect(Collectors.toSet()).containsAll(installationActivityTypes)) {
                errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0112, "Installation Activity Types"));
            }
        }

        if (OperatorType.AIRCRAFT_OPERATOR == operatorType) {
            validateMonitoringPlanId(accountDetails.getMonitoringPlanId(), errorDetails);
        }

        if (OperatorType.MARITIME_OPERATOR == operatorType) {
            // Monitoring plan id is not mandatory
            validateMonitoringPlanId(accountDetails.getMonitoringPlanId(), errorDetails);
            createAccountValidator.validateMandatoryField(accountDetails.getCompanyImoNumber(), "Company IMO Number", 256,
                    accountService::maritimeImoExists, IntegrationEventError.ERROR_0110, errorDetails);

            createAccountValidator.validateMandatoryField(accountDetails.getFirstYearOfVerifiedEmissions(), "First Year Of Verified Emission",
                    year -> year < maritimeStartingYear, IntegrationEventError.ERROR_0106, errorDetails);

        } else {
            createAccountValidator.validateMandatoryField(accountDetails.getFirstYearOfVerifiedEmissions(), "First Year Of Verified Emission",
                    year -> year < 2021, IntegrationEventError.ERROR_0111, errorDetails);
        }

        Optional.ofNullable(accountDetails.getFirstYearOfVerifiedEmissions())
                .map(Object::toString)
                .filter(s -> !s.matches("^\\d{4}$")) // the year should be 4 digits
                .ifPresent(year -> errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0101, "First Year Of Verified Emission")));

        createAccountValidator.validateMandatoryField(accountDetails.getRegulator(), "Regulator",
                v -> Arrays.stream(RegulatorType.values()).map(Enum::name).noneMatch(r -> r.equals(v)), IntegrationEventError.ERROR_0108, errorDetails);

        AccountHolderMessage accountHolder = event.getAccountHolder();

        createAccountValidator.validateMandatoryField(accountHolder.getName(), "Account Holder Name", 256, errorDetails);
        createAccountValidator.validateMandatoryField(accountHolder.getAddressLine1(), "Address Line 1", 256, errorDetails);
        createAccountValidator.validateLength(accountHolder.getAddressLine2(), "Address Line 2", 256, errorDetails);
        createAccountValidator.validateMandatoryField(accountHolder.getTownOrCity(), "Town Or City", 256, errorDetails);
        createAccountValidator.validateLength(accountHolder.getStateOrProvince(), "State or Province", 256, errorDetails);

        createAccountValidator.validateMandatoryField(accountHolder.getCountry(), "Country", errorDetails);
        Optional.ofNullable(accountHolder.getCountry())
                .filter(country -> !countryMap.containsCountryCode(country))
                .ifPresent(country -> errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0107, country)));

        createAccountValidator.validateLength(accountHolder.getPostalCode(), "Postal Code", 64, errorDetails);

        createAccountValidator.validateMandatoryField(accountHolder.getAccountHolderType(), "Account Holder Type",
                type -> Arrays.stream(AccountHolderType.values()).noneMatch(s -> s.name().equals(type)), IntegrationEventError.ERROR_0109, errorDetails);

        if (!AccountHolderType.INDIVIDUAL.name().equals(accountHolder.getAccountHolderType())) {
            createAccountValidator.validateMandatoryField(accountHolder.getCrnNotExist(), "Registration Number Not Exist", errorDetails);

            if (Boolean.TRUE.equals(accountHolder.getCrnNotExist())) {
                if (isNullOrEmptyString(accountHolder.getCrnJustification())){
                    errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0104, "CRN Justification"));
                }
                createAccountValidator.validateLength(accountHolder.getCrnJustification(), "CRN Justification", 1024, errorDetails);
            }
            if (Boolean.FALSE.equals(accountHolder.getCrnNotExist())) {
                if (isNullOrEmptyString(accountHolder.getCompanyRegistrationNumber())){
                    errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0104, "CRN"));
                }
                createAccountValidator.validateLength(accountHolder.getCompanyRegistrationNumber(), "CRN", 255, errorDetails);
            }
        }

        return errorDetails;
    }

    private void validateMonitoringPlanId(String monitoringPlanId, List<IntegrationEventErrorDetails> errorDetails) {
       createAccountValidator.validateLength(monitoringPlanId, "Monitoring Plan ID", 256, errorDetails);
        Optional.ofNullable(monitoringPlanId)
                .filter(accountService::maritimeMonitoringPlanIdExists)
                .ifPresent(id ->
                        errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0105, id)));
    }
}
