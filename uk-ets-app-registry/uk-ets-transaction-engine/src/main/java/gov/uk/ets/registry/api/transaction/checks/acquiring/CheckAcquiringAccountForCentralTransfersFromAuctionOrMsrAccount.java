package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * The acquiring account must be UK Total Quantity Account when the transferring account is either UK Auction account
 * or UK Market Stability Reserve account.
 */
@Service("check2017")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountForCentralTransfersFromAuctionOrMsrAccount extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        boolean isTransferringAccountUkAuctionOrUkMsr =
            List.of(RegistryAccountType.UK_AUCTION_ACCOUNT, RegistryAccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT)
                .contains(getTransferringAccount(context).getRegistryAccountType());

        List<RegistryAccountType> allowedAcquiringAccountTypes = List.of(
            RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT
        );

        if (isTransferringAccountUkAuctionOrUkMsr &&
            !allowedAcquiringAccountTypes.contains(getAcquiringAccount(context).getRegistryAccountType())) {
            addError(context, "The acquiring account must be UK Total Quantity Account when the transferring account " +
                "is either UK Auction account or UK Market Stability Reserve account \n");
        }
    }

}
