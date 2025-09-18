package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Value of Search criteria on filtering accounts.
 */
@Getter
@Builder
public class AccountFilter {
    /**
     * The account identifier criterion.
     */
    private final String accountFullIdentifierOrName;

    /**
     * The account statuses criterion.
     */
    private final List<AccountStatus> accountStatuses;

    /**
     * The account type criterion.
     */
    @Setter
    private List<AccountType> accountTypes;

    /**
     * The account holder name criterion.
     */
    private final String accountHolderName;

    /**
     * The account identifiers for an authorized user.
     */
    @Setter
    private Set<String> accountFullIdentifiers;

    /**
     * The compliance status criterion
     */
    private final ComplianceStatus complianceStatus;

    /**
     * The permit identifier of the installation
     * or the monitoring plan identifier of account.
     */
    private final String permitOrMonitoringPlanIdentifier;

    /**
     * The urid (unique business identifier of user)
     * of the authorized representative of account
     */
    @Setter
    private String authorizedRepresentativeUrid;

    /**
     * The regulator of account
     */
    private final RegulatorType regulatorType;

    /**
     * The excluded account access states.
     */
    @Setter
    private List<AccountAccessState> excludedAccessStates;

    /**
     * The excluded account statuses.
     */
    @Setter
    private List<AccountStatus> excludedAccountStatuses;

    /**
     * The allocation status.
     */
    @Setter
    private AllocationClassification allocationClassification;

    /**
     * The allocation withhold status.
     */
    @Setter
    private AllocationStatusType allocationWithholdStatus;

    /**
     * The year of the exclusion from emissions.
     */
    @Setter
    private Long excludedForYear;

    /**
     * Excludes the provided account statuses.
     *
     * @param accountStatuses The account statuses to exclude.
     */
    public void addExcludedAccountStatus(AccountStatus... accountStatuses) {
        if (excludedAccountStatuses == null) {
            excludedAccountStatuses = new ArrayList<>();
        }
        excludedAccountStatuses.addAll(List.of(accountStatuses));
    }

    /**
     * The Installation or Aircraft Operator ID.
     */
    @Setter
    private String operatorId;

    /**
     * Determines if the current user has limited scope, e.g. a non-admin user.
     */
    @Setter()
    @Accessors(fluent = true)
    private boolean hasLimitedScope;

    private String imo;
}
