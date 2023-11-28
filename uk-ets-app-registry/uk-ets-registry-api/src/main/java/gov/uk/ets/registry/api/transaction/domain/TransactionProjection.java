package gov.uk.ets.registry.api.transaction.domain;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import lombok.Getter;


/**
 * Projection of Transaction.
 */
@Getter
public class TransactionProjection {
    /**
     * The unique business identifier, e.g. GB40140.
     */
    private String identifier;

    /**
     * The type.
     */
    private TransactionType type;

    /**
     * The status.
     */
    private TransactionStatus status;

    /**
     * The date when the transaction status was last updated.
     */
    private Date lastUpdated;

    /**
     * The total quantity transferred in the context of this transaction.
     */
    private Long quantity;

    /**
     * The unit types involved in this transaction.
     * <ol>
     *     <li>Multiple, if many unit types are transacted.</li>
     *     <li>A specific unit type otherwise.</li>
     * </ol>
     */
    private UnitType unitType;

    /**
     * The transferring account projection.
     */
    private AccountProjection transferringAccount;

    /**
     * The acquiring account projection.
     */
    private AccountProjection acquiringAccount;

    private Date transactionStarted;

    /**
     * The balance after transaction completion.
     * Only applicable when viewed from the transactions of a specific account.
     */
    private Long runningBalanceQuantity;

    /**
     * The unit types  of the balance after transaction completion.
     * Only applicable when viewed from the transactions of a specific account.
     * <ol>
     *     <li>Multiple, if many unit types are transacted.</li>
     *     <li>A specific unit type otherwise.</li>
     * </ol>
     */
    private UnitType runningBalanceUnitType;
    
    private String reverses;
    
    private String reversedBy;

    @QueryProjection
    public TransactionProjection(String identifier, TransactionType type, TransactionStatus status, Date lastUpdated,
                                 Long quantity, UnitType unitType, AccountProjection transferringAccount,
                                 AccountProjection acquiringAccount, Date transactionStarted,
                                 String reverses, String reversedBy,
                                 Long transactionAccountBalance, UnitType transactionAccountBalanceUnitType) {
        this.identifier = identifier;
        this.type = type;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.quantity = quantity;
        this.unitType = unitType;
        this.transferringAccount = transferringAccount;
        this.acquiringAccount = acquiringAccount;
        this.transactionStarted = transactionStarted;
        this.reverses = reverses;
        this.reversedBy = reversedBy;
        this.runningBalanceQuantity = transactionAccountBalance;
        this.runningBalanceUnitType = transactionAccountBalanceUnitType;  
    }
    
    
    @QueryProjection
    public TransactionProjection(String identifier, TransactionType type, TransactionStatus status, Date lastUpdated,
                                 Long quantity, UnitType unitType, AccountProjection transferringAccount,
                                 AccountProjection acquiringAccount, Date transactionStarted, String reverses, String reversedBy) {
        this.identifier = identifier;
        this.type = type;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.quantity = quantity;
        this.unitType = unitType;
        this.transferringAccount = transferringAccount;
        this.acquiringAccount = acquiringAccount;
        this.transactionStarted = transactionStarted;  
        this.reverses = reverses;
        this.reversedBy = reversedBy;
    }

}
