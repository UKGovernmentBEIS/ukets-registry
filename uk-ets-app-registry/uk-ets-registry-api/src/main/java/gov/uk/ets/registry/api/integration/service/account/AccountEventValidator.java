package gov.uk.ets.registry.api.integration.service.account;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.common.CountryMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@Component
@RequiredArgsConstructor
public class AccountEventValidator {

    @Value("${emissions-maritime-starting-year}")
    private Integer maritimeStartingYear;

    private final CountryMap countryMap;
    private final AccountService accountService;

    public List<IntegrationEventErrorDetails> validate(AccountOpeningEvent event, OperatorType operatorType) {

        List<IntegrationEventErrorDetails> errorDetails = new ArrayList<>();
        AccountDetailsMessage accountDetails = event.getAccountDetails();

        validateMandatoryField(accountDetails.getEmitterId(), "Emitter ID", 255,
            accountService::emitterIdExists, IntegrationEventError.ERROR_0102, errorDetails);

        if (OperatorType.INSTALLATION == operatorType) {
            validateMandatoryField(accountDetails.getPermitId(), "Permit ID", 256,
                accountService::installationPermitIdExists, IntegrationEventError.ERROR_0105, errorDetails);

            validateMandatoryField(accountDetails.getInstallationName(), "Installation Name", 256, errorDetails);

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
            validateMandatoryField(accountDetails.getCompanyImoNumber(), "Company IMO Number", 256,
                accountService::maritimeImoExists, IntegrationEventError.ERROR_0110, errorDetails);

            validateMandatoryField(accountDetails.getFirstYearOfVerifiedEmissions(), "First Year Of Verified Emission",
                year -> year < maritimeStartingYear, IntegrationEventError.ERROR_0106, errorDetails);
        } else {
            validateMandatoryField(accountDetails.getFirstYearOfVerifiedEmissions(), "First Year Of Verified Emission",
                year -> year < 2021, IntegrationEventError.ERROR_0111, errorDetails);
        }

        Optional.ofNullable(accountDetails.getFirstYearOfVerifiedEmissions())
            .map(Object::toString)
            .filter(s -> !s.matches("^\\d{4}$")) // the year should be 4 digits
            .ifPresent(year -> errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0101, "First Year Of Verified Emission")));

        validateMandatoryField(accountDetails.getRegulator(), "Regulator",
            v -> Arrays.stream(RegulatorType.values()).map(Enum::name).noneMatch(r -> r.equals(v)), IntegrationEventError.ERROR_0108, errorDetails);

        AccountHolderMessage accountHolder = event.getAccountHolder();

        validateMandatoryField(accountHolder.getName(), "Account Holder Name", 256, errorDetails);
        validateMandatoryField(accountHolder.getAddressLine1(), "Address Line 1", 256, errorDetails);
        validateLength(accountHolder.getAddressLine2(), "Address Line 2", 256, errorDetails);
        validateMandatoryField(accountHolder.getTownOrCity(), "Town Or City", 256, errorDetails);
        validateLength(accountHolder.getStateOrProvince(), "State or Province", 256, errorDetails);

        validateMandatoryField(accountHolder.getCountry(), "Country", errorDetails);
        Optional.ofNullable(accountHolder.getCountry())
            .filter(country -> !countryMap.containsCountryCode(country))
            .ifPresent(country -> errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0107, country)));

        validateLength(accountHolder.getPostalCode(), "Postal Code", 64, errorDetails);

        validateMandatoryField(accountHolder.getAccountHolderType(), "Account Holder Type",
            type -> Arrays.stream(AccountHolderType.values()).noneMatch(s -> s.name().equals(type)), IntegrationEventError.ERROR_0109, errorDetails);

        if (!AccountHolderType.INDIVIDUAL.name().equals(accountHolder.getAccountHolderType())) {
            validateMandatoryField(accountHolder.getCrnNotExist(), "Registration Number Not Exist", errorDetails);

            if (Boolean.TRUE.equals(accountHolder.getCrnNotExist())) {
                if (isNullOrEmptyString(accountHolder.getCrnJustification())){
                    errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0104, "CRN Justification"));
                }
                validateLength(accountHolder.getCrnJustification(), "CRN Justification", 1024, errorDetails);
            }
            if (Boolean.FALSE.equals(accountHolder.getCrnNotExist())) {
                if (isNullOrEmptyString(accountHolder.getCompanyRegistrationNumber())){
                    errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0104, "CRN"));
                }
                validateLength(accountHolder.getCompanyRegistrationNumber(), "CRN", 255, errorDetails);
            }
        }

        return errorDetails;
    }

    private void validateMonitoringPlanId(String monitoringPlanId, List<IntegrationEventErrorDetails> errorDetails) {
        validateLength(monitoringPlanId, "Monitoring Plan ID", 256, errorDetails);
        Optional.ofNullable(monitoringPlanId)
            .filter(accountService::maritimeMonitoringPlanIdExists)
            .ifPresent(id ->
                errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0105, id)));
    }

    private <T> void validateMandatoryField(T value,
                                           String fieldName,
                                           Predicate<T> businessCheck,
                                           IntegrationEventError error,
                                           List<IntegrationEventErrorDetails> errorDetails) {

        if (isNullOrEmptyString(value)) {
            errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0103, fieldName));
            return;
        }

        if (businessCheck.test(value)) {
            errorDetails.add(new IntegrationEventErrorDetails(error, value.toString()));
        }

    }

    private void validateMandatoryField(String value,
                                        String fieldName,
                                        int length,
                                        Predicate<String> businessCheck,
                                        IntegrationEventError error,
                                        List<IntegrationEventErrorDetails> errorDetails) {

        validateMandatoryField(value, fieldName, businessCheck, error, errorDetails);

        validateLength(value, fieldName, length, errorDetails);

    }

    private void validateLength(String value, String fieldName, int length,
                                  List<IntegrationEventErrorDetails> errorDetails) {
        Optional.ofNullable(value)
            .filter(s -> s.length() > length)
            .ifPresent(s -> errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0101, fieldName)));
    }

    private <T> void validateMandatoryField(T value, String fieldName, List<IntegrationEventErrorDetails> errorDetails) {
        validateMandatoryField(value, fieldName, o -> false, null, errorDetails);
    }

    private void validateMandatoryField(String value, String fieldName, int length, List<IntegrationEventErrorDetails> errorDetails) {
        validateMandatoryField(value, fieldName, length, o -> false, null, errorDetails);
    }

    private boolean isNullOrEmptyString(Object value) {
        return value == null || value instanceof String str && str.isEmpty();
    }
}
