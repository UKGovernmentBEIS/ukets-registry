package gov.uk.ets.registry.api.integration.service.withhold;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.service.AccountAllocationService;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class WithholdEventValidator {

    private static final List<AccountStatus> INVALID_ACCOUNT_STATUES =
            List.of(AccountStatus.CLOSED, AccountStatus.CLOSURE_PENDING, AccountStatus.TRANSFER_PENDING);

    private final AccountRepository accountRepository;
    private final AccountAllocationService accountAllocationService;

    public List<IntegrationEventErrorDetails> validate(AccountWithholdUpdateEvent event) {

        List<IntegrationEventErrorDetails> errors = new ArrayList<>();

        Long operatorId = event.getRegistryId();
        validateMandatoryField(operatorId, "Registry Id", errors);
        validateMandatoryField(event.getWithholdFlag(), "Withhold Flag", errors);

        Year reportingYear = event.getReportingYear();
        validateMandatoryField(reportingYear, "Reporting Year", errors);

        Predicate<Year> validYearPredicate = year -> year.toString().matches("^\\d{4}$");
        if (reportingYear != null && !validYearPredicate.test(reportingYear)) {
            errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0501, "Reporting Year"));
        }

        if (operatorId == null) {
            return errors; // No reason to continue
        }

        Optional<Account> optionalAccount = accountRepository.findByCompliantEntityIdentifier(operatorId);
        if (optionalAccount.isEmpty()) {
            errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0503, operatorId.toString()));
            return errors; // No reason to continue
        }

        Account account = optionalAccount.get();
        AccountStatus accountStatus = account.getAccountStatus();
        if (INVALID_ACCOUNT_STATUES.contains(accountStatus)) {
            errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0506, accountStatus.name()));
        }

        if (reportingYear != null) {
            CompliantEntity compliantEntity = account.getCompliantEntity();
            Integer firstYear = compliantEntity.getStartYear();
            if (reportingYear.isBefore(Year.of(firstYear))) {
                errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0504, reportingYear.toString()));
                return errors; // No reason to continue
            }
            Integer lastYear = compliantEntity.getEndYear();
            if (lastYear != null && reportingYear.isAfter(Year.of(lastYear))) {
                errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0505, reportingYear.toString()));
                return errors; // No reason to continue
            }

            if (Boolean.TRUE.equals(event.getWithholdFlag()) && validYearPredicate.test(reportingYear)) {
                Map<Integer, AllocationStatusType> statuses = accountAllocationService.getAccountAllocationStatus(account.getIdentifier());
                if (!statuses.containsKey(reportingYear.getValue())) {
                    errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0508, reportingYear.toString()));
                }
            }
        }

        return errors;
    }

    private <T> void validateMandatoryField(T value,
                                            String fieldName,
                                            List<IntegrationEventErrorDetails> errorDetails) {

        if (value == null) {
            errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0502, fieldName));
        }
    }
}
