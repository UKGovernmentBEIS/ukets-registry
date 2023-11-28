package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;


/**
 * The Transaction message.
 */
@Getter
@Builder
public class TransactionNotification implements Serializable {

    /**
     * Factory method to create notification from transaction
     * @param transaction The transaction
     * @return The notification
     */
    public static TransactionNotification from(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return TransactionNotification.builder()
            .identifier(transaction.getIdentifier())
            .type(transaction.getType())
            .quantity(transaction.getQuantity())
            .acquiringAccountIdentifier(transaction.getAcquiringAccount() == null ? null
                : transaction.getAcquiringAccount().getAccountIdentifier())
            .acquiringAccountType(transaction.getAcquiringAccount() == null ? null
                : transaction.getAcquiringAccount().getAccountType())
            .acquiringAccountFullIdentifier(transaction.getAcquiringAccount() == null ? null
                : transaction.getAcquiringAccount().getAccountFullIdentifier())
            .transferringAccountIdentifier(transaction.getTransferringAccount() == null ? null
                : transaction.getTransferringAccount().getAccountIdentifier())
            .transferringAccountType(transaction.getTransferringAccount() == null ? null
                : transaction.getTransferringAccount().getAccountType())
            .transferringAccountFullIdentifier(transaction.getTransferringAccount() == null ? null
                : transaction.getTransferringAccount().getAccountFullIdentifier())
            .started(transaction.getStarted())
            .blocks(transaction.getBlocks())
            .lastUpdated(transaction.getLastUpdated())
            .executionDate(transaction.getExecutionDate())
            .unitType(transaction.getUnitType())
            .build();
    }

    /**
     * The transaction unique business identifier, e.g. GB40140.
     */
    private String identifier;
    /**
     * The type.
     */
    private TransactionType type;
    /**
     * The transaction total quantity transferred in the context of this transaction.
     */
    private Long quantity;
    /**
     * The unique acquiring account business identifier, e.g. 10455.
     */
    private Long acquiringAccountIdentifier;
    /**
     * The acquiring account type.
     */
    private KyotoAccountType acquiringAccountType;
    /**
     * The acquiring account full identifier
     */
    private String acquiringAccountFullIdentifier;
    /**
     * The transferring account identifier
     */
    private Long transferringAccountIdentifier;
    /**
     * The transferring account type
     */
    private KyotoAccountType transferringAccountType;
    /**
     * The transferring account full identifier
     */
    private String transferringAccountFullIdentifier;
    /**
     * The transaction started date
     */
    private Date started;
    /**
     * The transaction last updated date
     */
    private Date lastUpdated;
    /**
     * The transaction execution date
     */
    private LocalDateTime executionDate;
    /**
     * The transaction unit type
     */
    private UnitType unitType;
    /**
     * The transaction blocks
     */
    private List<TransactionBlock> blocks;
}
