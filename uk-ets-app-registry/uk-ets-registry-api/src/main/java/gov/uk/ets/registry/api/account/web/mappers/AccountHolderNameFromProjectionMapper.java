package gov.uk.ets.registry.api.account.web.mappers;

import gov.uk.ets.registry.api.account.shared.AccountProjection;

/**
 * Account holder full name mapper
 */
public class AccountHolderNameFromProjectionMapper {

    /**
     * Builds the full name of the account holder
     * @param accountProjection The accountHolder
     * @return The full name of account holder
     */
    public String map(AccountProjection accountProjection) {
        if(accountProjection == null) {
            return null;
        }
        String fullName;
        if (accountProjection.getAccountHolderFirstName() != null && accountProjection.getAccountHolderLastName() != null) {
            fullName =  accountProjection.getAccountHolderFirstName() + " " + accountProjection.getAccountHolderLastName();
        } else {
            fullName = accountProjection.getAccountHolderName();
        }
        return fullName;
    }

}
