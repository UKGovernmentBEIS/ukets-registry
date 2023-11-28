package uk.gov.ets.transaction.log.messaging.types;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ets.transaction.log.domain.TransactionBlock;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;

/**
 * The Transaction message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionNotification implements Serializable {
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
    private String acquiringAccountType;
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
    private String transferringAccountType;
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
