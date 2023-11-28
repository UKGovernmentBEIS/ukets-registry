package gov.uk.ets.registry.api.authz.ruleengine.features.ar;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The data that are used on AR update requests business rules checks.
 */
@Getter
@Setter
public class ARBusinessSecurityStoreSlice {
    /**
     * The ARs of the account.
     */
    private List<AccountAccess> accountARs;
    /**
     * The unique business identifier of the AR candidate user.
     */
    private String candidateUrid;
    /**
     * The unique business identifier of the AR user that is going to be replaced.
     */
    private String predecessorUrid;
    /**
     * The pending account AR update requests.
     */
    private List<ARUpdateAction> pendingARUpdateRequests;

    /**
     * The pending account AR add requests.
     */
    private List<ARUpdateAction> pendingARAddRequests;
    
    /**
     * The candidate user's roles.
     */
    private List<UserRole> candidateUserRoles;
}
