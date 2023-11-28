package gov.uk.ets.registry.api.authz.ruleengine.features.allocation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocationSecurityStoreSlice {

    private boolean enoughUnitsOnAllocationAccounts;
}
