package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.task.shared.EndUserSearch;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * The transaction search filter value object
 */
@Getter
@Builder
public class TransactionFilter {

    /**
     * The transaction id
     */
    private String transactionId;

    /**
     * The transaction type
     */
    private TransactionType transactionType;

    /**
     * The transaction status
     */
    private TransactionStatus transactionStatus;

    /**
     * The transaction last update date (from)
     */
    private Date transactionLastUpdateDateFrom;

    /**
     * The transaction last update date (until)
     */
    private Date transactionLastUpdateDateTo;

    /**
     * The transferring account number
     */
    private String transferringAccountNumber;

    /**
     * The acquiring account number
     */
    private String acquiringAccountNumber;

    /**
     * The acquiring account type
     */
    private List<AccountType> acquiringAccountTypes;

    /**
     * The transferring account type
     */
    private List<AccountType> transferringAccountTypes;

    /**
     * The unit type
     */
    private UnitType unitType;

    /**
     * The initiator user id
     */
    private String initiatorUserId;

    /**
     * The approver user id
     */
    private String approverUserId;

    /**
     * The transactional proposal date (from)
     */
    private Date transactionalProposalDateFrom;

    /**
     * The transactional proposal date (until)
     */
    private Date transactionalProposalDateTo;

    /**
     * Flag that indicates that the search should be performed for non admin user
     */
    private boolean enrolledNonAdmin;

    /**
     * The authorized representative urid of the non admin user
     */
    private String authorizedRepresentativeUrid;

    /**
     * The value of Transferring account type
     */
    private String transferringAccountTypeOption;

    /**
     * The value of Acquiring account type
     */
    private String acquiringAccountTypeOption;

    /**
     * The information about the user that performs the search.
     * */
    private EndUserSearch endUserSearch;

    /**
     * True if we want to display running balances.
     */
    private boolean showRunningBalances;
    
    private Boolean reversed;
    
    /**
     * @return the boolean flag that indicates if the transaction related task participates in
     * filtering
     */
    public boolean taskParticipatesInFilter() {
        return approverUserId != null || initiatorUserId != null
            || transactionalProposalDateFrom != null || transactionalProposalDateTo != null;
    }
}
