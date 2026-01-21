package gov.uk.ets.registry.api.integration.service.account.validators;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Component
public class UpdateAccountFormatValidator {

    private static final int LEN_DEFAULT = 256;
    private static final int LEN_CRN = 255;
    private static final int LEN_CRN_JUSTIFICATION = 1024;

    private static final Set<String> VALID_ACCOUNT_TYPES = Set.of(
            "OPERATOR_HOLDING_ACCOUNT",
            "AIRCRAFT_OPERATOR_HOLDING_ACCOUNT",
            "MARITIME_OPERATOR_HOLDING_ACCOUNT"
    );

    public List<IntegrationEventErrorDetails> validate(AccountUpdatingEvent event) {

        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        // ROOT CHECKS (0303)
        if (event == null) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0303, "Event"
            ));
            return errors;
        }

        UpdateAccountDetailsMessage details = event.getAccountDetails();
        AccountHolderMessage accountHolder = event.getAccountHolder();

        if(details != null) {
            // ACCOUNT TYPE (SERVICE)
            validateFormat(
                    details.getAccountType(),
                    "Account Type",
                    errors,
                    s -> s.matches("^[A-Z_]+$"),       // FORMAT
                    VALID_ACCOUNT_TYPES::contains      // ALLOWED VALUES
            );

            // REGISTRY ID
            validateFormat(
                    details.getRegistryId(),
                    "Registry ID",
                    errors,
                    s -> s.matches("^\\d+$"),
                    s -> true
            );

            // INSTALLATION NAME
            validateLength(details.getInstallationName(), "Installation Name", LEN_DEFAULT, errors);

            // PERMIT ID
            validateLength(details.getPermitId(), "Permit ID", LEN_DEFAULT, errors);


            // MONITORING PLAN ID
            validateLength(details.getMonitoringPlanId(), "Monitoring Plan ID", LEN_DEFAULT, errors);


            // IMO NUMBER
            validateLength(details.getCompanyImoNumber(), "Company IMO number", LEN_DEFAULT, errors);

            // FYVE — must be 4-digit year (format)
            if (details.getFirstYearOfVerifiedEmissions() != null) {
                String y = details.getFirstYearOfVerifiedEmissions().toString();
                if (!y.matches("^\\d{4}$")) {
                    errors.add(new IntegrationEventErrorDetails(
                            IntegrationEventError.ERROR_0301,
                            "First Year Of Verified Emission"
                    ));
                }
            }
        } else {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0303, "Account Details"
            ));
        }


        if (accountHolder != null) {
            // ACCOUNT HOLDER FORMAT VALIDATION
            validateLength(accountHolder.getName(), "Account Holder Name", LEN_DEFAULT, errors);
            validateLength(accountHolder.getAddressLine1(), "Address Line 1", LEN_DEFAULT, errors);
            validateLength(accountHolder.getAddressLine2(), "Address Line 2", LEN_DEFAULT, errors);
            validateLength(accountHolder.getTownOrCity(), "Town Or City", LEN_DEFAULT, errors);
            validateLength(accountHolder.getStateOrProvince(), "State or Province", LEN_DEFAULT, errors);
            //Skip postal code and country


            // ORGANISATION: CRN rules (format only)
            if (AccountHolderType.ORGANISATION.name().equals(accountHolder.getAccountHolderType())) {

                if (Boolean.FALSE.equals(accountHolder.getCrnNotExist())) {
                    validateLength(accountHolder.getCompanyRegistrationNumber(), "CRN", LEN_CRN, errors);
                }

                if (Boolean.TRUE.equals(accountHolder.getCrnNotExist())) {
                    validateLength(accountHolder.getCrnJustification(),
                            "CRN Justification",
                            LEN_CRN_JUSTIFICATION,
                            errors
                    );
                }
            }
        } else {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0303, "Account Holder"
            ));
        }
        return errors;
    }


    // Helper Methods
    private void validateFormat(String value,
                                String fieldName,
                                List<IntegrationEventErrorDetails> errors,
                                Predicate<String> formatCheck,
                                Predicate<String> allowedCheck) {

        if (value == null) return;

        if (!formatCheck.test(value)) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0301, fieldName
            ));
            return;
        }

        if (!allowedCheck.test(value)) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0316, fieldName
            ));
        }
    }

    private void validateLength(String value,
                                String fieldName,
                                int maxLength,
                                List<IntegrationEventErrorDetails> errors) {

        if (value != null && value.length() > maxLength) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0301, fieldName
            ));
        }
    }
}

