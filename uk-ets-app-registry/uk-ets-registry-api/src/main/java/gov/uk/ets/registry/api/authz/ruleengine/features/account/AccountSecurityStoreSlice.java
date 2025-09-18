package gov.uk.ets.registry.api.authz.ruleengine.features.account;

import gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsDTO;
import java.util.List;

import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import lombok.Getter;
import lombok.Setter;

/**
 * The data that are used on account  business rules checks.
 */
@Getter
@Setter
public class AccountSecurityStoreSlice {
    private Long pendingTasks;
    private Long activeTransactions;
    private Long pendingActivationTrustedAccounts;
    private Long pendingComplianceEntityUpdate;
    private List<VerifiedEmissionsDTO> verifiedEmissionsList;
    protected List<TrustedAccount> linkedPendingTrustedAccounts;
}
