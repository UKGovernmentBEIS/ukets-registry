package gov.uk.ets.registry.api.account.web.mappers;

import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.web.model.search.AccountSearchCriteria;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mapper which is responsible for translating the {@link AccountSearchCriteria} web client input to
 * the {@link AccountFilter} value object.
 */
public class AccountFilterMapper {

    public static final String ALL_EXCEPT_CLOSED = "ALL_EXCEPT_CLOSED";

    /**
     * Maps a {@link AccountSearchCriteria} to a {@link AccountFilter}.
     *
     * @param criteria The web client input
     * @return The {@link AccountFilter} value object
     */
    public AccountFilter map(AccountSearchCriteria criteria) {
        return AccountFilter
            .builder()
            .accountFullIdentifierOrName(criteria.getAccountIdOrName())
            .accountTypes(SearchFiltersUtils.mapToAccountTypes(criteria.getAccountType()))
            .accountHolderName(criteria.getAccountHolderName())
            .permitOrMonitoringPlanIdentifier(criteria.getPermitOrMonitoringPlanIdentifier())
            .accountStatuses(getAccountStatuses(criteria.getAccountStatus()))
            .regulatorType(SearchFiltersUtils.mapToEnum(RegulatorType.values(), criteria.getRegulatorType()))
            .complianceStatus(SearchFiltersUtils.mapToEnum(ComplianceStatus.values(), criteria.getComplianceStatus()))
            .authorizedRepresentativeUrid(criteria.getAuthorizedRepresentativeUrid())
            .allocationClassification(criteria.getAllocationStatus())
            .allocationWithholdStatus(criteria.getAllocationWithholdStatus())
            .installationOrAircraftOperatorId(criteria.getInstallationOrAircraftOperatorId())
            .excludedForYear(criteria.getExcludedForYear())
            .build();
    }

    private List<AccountStatus> getAccountStatuses(String optionCode) {
        if (optionCode == null) {
            return null;
        }
        if (ALL_EXCEPT_CLOSED.equals(optionCode)) {
            return Stream.of(AccountStatus.values()).filter(status ->
                    !Set.of(AccountStatus.CLOSED, AccountStatus.PROPOSED, AccountStatus.REJECTED).contains(status))
                .collect(Collectors.toList());
        }
        return List.of(AccountStatus.valueOf(optionCode));
    }
}
