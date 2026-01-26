package gov.uk.ets.registry.api.integration.service.account.validators;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.common.CountryMap;
import gov.uk.ets.registry.api.integration.consumer.ValidatorOperationType;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CommonAccountValidator {

    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final ValidatorOperationType operationType;
    private final CountryMap countryMap;

    public CommonAccountValidator(
            ValidatorOperationType accountOperationType,
            CountryMap countryMap) {
        this.operationType = accountOperationType;
        this.countryMap = countryMap;
    }

    public void validateAccountHolder(AccountHolderMessage accountHolder, List<IntegrationEventErrorDetails> errorDetails) {
        //Validate addressFields
        validateMandatoryField(accountHolder.getName(), "Account Holder Name", 256, errorDetails);
        validateMandatoryField(accountHolder.getAddressLine1(), "Address Line 1", 256, errorDetails);
        validateLength(accountHolder.getAddressLine2(), "Address Line 2", 256, errorDetails);
        validateMandatoryField(accountHolder.getTownOrCity(), "Town Or City", 256, errorDetails);
        validateLength(accountHolder.getStateOrProvince(), "State or Province", 256, errorDetails);

        if(operationType.equals(ValidatorOperationType.CREATE_ACCOUNT)) {
            validateMandatoryField(accountHolder.getCountry(), "Country", errorDetails);
            Optional.ofNullable(accountHolder.getCountry())
                    .filter(country -> !countryMap.containsCountryCode(country))
                    .ifPresent(country -> errorDetails.add(new IntegrationEventErrorDetails(
                            resolveError(IntegrationEventError.ERROR_0107), country)));

            validateLength(accountHolder.getPostalCode(), "Postal Code", 64, errorDetails);
        }

        validateMandatoryField(accountHolder.getAccountHolderType(), "Account Holder Type",
                type -> Arrays.stream(AccountHolderType.values()).noneMatch(s -> s.name().equals(type)),
                resolveError(IntegrationEventError.ERROR_0109), errorDetails);

        if (!AccountHolderType.INDIVIDUAL.name().equals(accountHolder.getAccountHolderType())) {
            validateMandatoryField(accountHolder.getCrnNotExist(), "Registration Number Not Exist", errorDetails);

            if (Boolean.TRUE.equals(accountHolder.getCrnNotExist())) {
                if (isNullOrEmptyString(accountHolder.getCrnJustification())){
                    errorDetails.add(new IntegrationEventErrorDetails(
                            resolveError(IntegrationEventError.ERROR_0104), "CRN Justification"));
                }
                validateLength(accountHolder.getCrnJustification(), "CRN Justification", 1024, errorDetails);
            }
            if (Boolean.FALSE.equals(accountHolder.getCrnNotExist())) {
                if (isNullOrEmptyString(accountHolder.getCompanyRegistrationNumber())){
                    errorDetails.add(new IntegrationEventErrorDetails(
                            resolveError(IntegrationEventError.ERROR_0104), "CRN"));
                }
                validateLength(accountHolder.getCompanyRegistrationNumber(), "CRN", 255, errorDetails);
            }
        }
    }

    public <T> void validateMandatoryField(T value,
                                            String fieldName,
                                            Predicate<T> businessCheck,
                                            IntegrationEventError error,
                                            List<IntegrationEventErrorDetails> errorDetails) {

        if (isNullOrEmptyString(value)) {
            errorDetails.add(new IntegrationEventErrorDetails(resolveError(IntegrationEventError.ERROR_0103), fieldName));
            return;
        }

        if (businessCheck.test(value)) {
            errorDetails.add(new IntegrationEventErrorDetails(error, fieldName));
        }

    }

    public void validateMandatoryField(String value,
                                       String fieldName,
                                       int length,
                                       Predicate<String> businessCheck,
                                       IntegrationEventError error,
                                       List<IntegrationEventErrorDetails> errorDetails) {

        validateMandatoryField(value, fieldName, businessCheck, error, errorDetails);

        validateLength(value, fieldName, length, errorDetails);

    }

    public void validateLength(String value, String fieldName, int length,
                                List<IntegrationEventErrorDetails> errorDetails) {
        Optional.ofNullable(value)
                .filter(s -> s.length() > length)
                .ifPresent(s -> errorDetails.add(new IntegrationEventErrorDetails(
                        resolveError(IntegrationEventError.ERROR_0101), fieldName)));
    }

    public <T> void validateMandatoryField(T value, String fieldName, List<IntegrationEventErrorDetails> errorDetails) {
        validateMandatoryField(value, fieldName, o -> false, null, errorDetails);
    }

    public void validateMandatoryField(String value, String fieldName, int length, List<IntegrationEventErrorDetails> errorDetails) {
        validateMandatoryField(value, fieldName, length, o -> false, null, errorDetails);
    }

    public boolean isNullOrEmptyString(Object value) {
        return value == null || value instanceof String str && str.isEmpty();
    }

    public void validateCountry(String country, List<IntegrationEventErrorDetails> errors) {

        String ref = "Country";
        if (country == null || country.isBlank()) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0303, ref
            ));
            return;
        }

        if (!country.matches("^[A-Za-z]{2}$")) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0301, ref
            ));
            return;
        }

        if (!countryMap.containsCountryCode(country.toUpperCase())) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0307, ref
            ));
        }
    }

    public void validatePostalCode(AccountHolderMessage accountHolder,
                                   List<IntegrationEventErrorDetails> errors) {

        String country = accountHolder.getCountry();
        String postal = accountHolder.getPostalCode();
        if (country != null && country.equals("UK")) {
            validateMandatoryField(postal, "Postal Code", errors);
        }
        validateLength(postal, "Postal Code", 64, errors);

    }

    public void validateEmail(String email,
                              String fieldName,
                              IntegrationEventError error,
                              List<IntegrationEventErrorDetails> errors) {

        if (email == null || email.isBlank()) return;

        if (!EMAIL_REGEX.matcher(email).matches()) {
            errors.add(new IntegrationEventErrorDetails(error, fieldName));
        }
    }

    private IntegrationEventError resolveError(IntegrationEventError baseError) {
        switch (operationType) {
            case UPDATE_ACCOUNT:
                // Map 01xx → 03xx codes
                if (baseError == IntegrationEventError.ERROR_0101) return IntegrationEventError.ERROR_0301;
                if (baseError == IntegrationEventError.ERROR_0103) return IntegrationEventError.ERROR_0303;
                if (baseError == IntegrationEventError.ERROR_0104) return IntegrationEventError.ERROR_0304;
                if (baseError == IntegrationEventError.ERROR_0106) return IntegrationEventError.ERROR_0306;
                if (baseError == IntegrationEventError.ERROR_0107) return IntegrationEventError.ERROR_0307;
                if (baseError == IntegrationEventError.ERROR_0109) return IntegrationEventError.ERROR_0309;
                if (baseError == IntegrationEventError.ERROR_0110) return IntegrationEventError.ERROR_0310;
                if (baseError == IntegrationEventError.ERROR_0111) return IntegrationEventError.ERROR_0311;
                break;
            case METS_CONTACTS:
                // Map 01xx → 03xx codes
                if (baseError == IntegrationEventError.ERROR_0101) return IntegrationEventError.ERROR_0701;
                if (baseError == IntegrationEventError.ERROR_0103) return IntegrationEventError.ERROR_0702;
                break;
            case CREATE_ACCOUNT:
            default:
                return baseError;
        }
        return baseError;
    }
}
