package gov.uk.ets.registry.api.integration.service.exemption;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExemptionEventValidator {

    private static final List<AccountStatus> INVALID_ACCOUNT_STATUES =
            List.of(AccountStatus.CLOSED, AccountStatus.CLOSURE_PENDING);

    private final AccountRepository accountRepository;

    public List<IntegrationEventErrorDetails> validate(AccountExemptionUpdateEvent event) {

        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        Long operatorId = event.getRegistryId();
        validateMandatoryField(operatorId, "Registry Id", errors);
        validateMandatoryField(event.getExemptionFlag(), "Exemption Flag", errors);

        Year reportingYear = event.getReportingYear();
        validateMandatoryField(reportingYear, "Reporting Year", errors);
        if (reportingYear != null) {
            if (!reportingYear.toString().matches("^\\d{4}$")) {
                errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0401, "Reporting Year"));
            } else if (reportingYear.isAfter(Year.now())) {
                errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0407, reportingYear.toString()));
            }
        }

        if (operatorId == null) {
            return errors; // No reason to continue
        }

        Optional<Account> optionalAccount = accountRepository.findByCompliantEntityIdentifier(operatorId);
        if (optionalAccount.isEmpty()) {
            errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0403, operatorId.toString()));
            return errors; // No reason to continue
        }

        Account account = optionalAccount.get();
        AccountStatus accountStatus = account.getAccountStatus();
        if (INVALID_ACCOUNT_STATUES.contains(accountStatus)) {
            errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0406, accountStatus.name()));
        }

        if (reportingYear != null) {
            CompliantEntity compliantEntity = account.getCompliantEntity();
            Integer firstYear = compliantEntity.getStartYear();
            if (reportingYear.isBefore(Year.of(firstYear))) {
                errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0404, reportingYear.toString()));
            }
            Integer lastYear = compliantEntity.getEndYear();
            if (lastYear != null && reportingYear.isAfter(Year.of(lastYear))) {
                errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0405, reportingYear.toString()));
            }
        }

        return errors;
    }

    private <T> void validateMandatoryField(T value,
                                            String fieldName,
                                            List<IntegrationEventErrorDetails> errorDetails) {

        if (value == null) {
            errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0402, fieldName));
        }
    }
}
