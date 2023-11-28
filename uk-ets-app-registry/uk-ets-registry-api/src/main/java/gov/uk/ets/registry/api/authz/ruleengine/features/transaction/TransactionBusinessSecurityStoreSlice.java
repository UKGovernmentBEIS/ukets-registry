package gov.uk.ets.registry.api.authz.ruleengine.features.transaction;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*
  The data that are used on transaction proposal requests business rules checks.
 */
public class TransactionBusinessSecurityStoreSlice {
    private TransactionType transactionType;
    private Account transferringAccount;
    private Account acquiringAccount;
    private String comment;
}
