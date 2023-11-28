package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * The acquiring account must be UK Total Quantity Account or UK Allocation Account when the transferring account is
 * UK New Entrants Reserve.
 */
@Service("check2015")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountForCentralTransfersFromNER extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        boolean isTransferringAccountUkNer = RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT
            .equals(getTransferringAccount(context).getRegistryAccountType());

        List<RegistryAccountType> allowedAcquiringAccountTypes = List.of(
            RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT,
            RegistryAccountType.UK_ALLOCATION_ACCOUNT
        );

        if (isTransferringAccountUkNer &&
            !allowedAcquiringAccountTypes.contains(getAcquiringAccount(context).getRegistryAccountType())) {
            addError(context, "The acquiring account must be UK Total Quantity Account or UK Allocation Account when " +
                "the transferring account is UK New Entrants Reserve");
        }
    }

}
