package gov.uk.ets.registry.api.transaction.checks;

import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class TransactionSummaryMock extends TransactionSummary {

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
     * The total quantity transferred in the context of this transaction.
     */
    private Long quantity;

    /**
     * Acquiring account: The unique account business identifier, e.g. 10455.
     */
    private Long acquiringAccountIdentifier;

    /**
     * Acquiring account: The Kyoto account type, e.g. PARTY_HOLDING_ACCOUNT.
     */
    private KyotoAccountType acquiringAccountType;

    /**
     * Acquiring account: The registry code, e.g. GB, JP etc.
     */
    private String acquiringAccountRegistryCode;

    /**
     * Acquiring account: The full account identifier, e.g. GB-100-10455-0-61, JP-100-23213 etc.
     */
    private String acquiringAccountFullIdentifier;

    /**
     * Acquiring account: The commitment period, e.g. 0
     */
    private Integer acquiringAccountCommitmentPeriod;

    /**
     * Transferring account: The unique account business identifier, e.g. 10944.
     */
    private Long transferringAccountIdentifier;

    /**
     * Transferring account: The Kyoto account type, e.g. PARTY_HOLDING_ACCOUNT.
     */
    private KyotoAccountType transferringAccountType;

    /**
     * Transferring account: The registry code, e.g. GB.
     */
    private String transferringRegistryCode;

    /**
     * Transferring account: The full account identifier, e.g. GB-100-10944-0-41.
     */
    private String transferringAccountFullIdentifier;

    /**
     * Acquiring account: The commitment period, e.g. 0
     */
    private Integer transferringAccountCommitmentPeriod;

    /**
     * The date when the transaction started.
     */
    private Date started;

    /**
     * The date when the transaction status was last updated.
     */
    private Date lastUpdated;

    /**
     * The ITL notification.
     */
    private ItlNotificationSummary itlNotificationSummary;

    /**
     * The transaction blocks.
     */
    private List<TransactionBlockSummary> blocks;

    public TransactionSummaryMock(String identifier, TransactionType type, TransactionStatus status, Long quantity, Long acquiringAccountIdentifier, KyotoAccountType acquiringAccountType, String acquiringAccountRegistryCode, String acquiringAccountFullIdentifier, Integer acquiringAccountCommitmentPeriod, Long transferringAccountIdentifier, KyotoAccountType transferringAccountType, String transferringRegistryCode, String transferringAccountFullIdentifier, Integer transferringAccountCommitmentPeriod, Date started, Date lastUpdated, ItlNotificationSummary itlNotificationSummary, List<TransactionBlockSummary> blocks) {
        this.identifier = identifier;
        this.type = type;
        this.status = status;
        this.quantity = quantity;
        this.acquiringAccountIdentifier = acquiringAccountIdentifier;
        this.acquiringAccountType = acquiringAccountType;
        this.acquiringAccountRegistryCode = acquiringAccountRegistryCode;
        this.acquiringAccountFullIdentifier = acquiringAccountFullIdentifier;
        this.acquiringAccountCommitmentPeriod = acquiringAccountCommitmentPeriod;
        this.transferringAccountIdentifier = transferringAccountIdentifier;
        this.transferringAccountType = transferringAccountType;
        this.transferringRegistryCode = transferringRegistryCode;
        this.transferringAccountFullIdentifier = transferringAccountFullIdentifier;
        this.transferringAccountCommitmentPeriod = transferringAccountCommitmentPeriod;
        this.started = started;
        this.lastUpdated = lastUpdated;
        this.itlNotificationSummary = itlNotificationSummary;
        this.blocks = blocks;
        setParentObjectState();
    }

    private void setParentObjectState() {
        super.setIdentifier(this.identifier);
        super.setType(this.type);
        super.setStatus(this.status);
        super.setQuantity(this.quantity);
        super.setAcquiringAccountIdentifier(this.acquiringAccountIdentifier);
        super.setAcquiringAccountType(this.acquiringAccountType);
        super.setAcquiringAccountRegistryCode(this.acquiringAccountRegistryCode);
        super.setAcquiringAccountFullIdentifier(this.acquiringAccountFullIdentifier);
        super.setAcquiringAccountCommitmentPeriod(this.acquiringAccountCommitmentPeriod);
        super.setTransferringAccountIdentifier(this.transferringAccountIdentifier);
        super.setTransferringAccountType(this.transferringAccountType);
        super.setTransferringRegistryCode(this.transferringRegistryCode);
        super.setTransferringAccountFullIdentifier(this.transferringAccountFullIdentifier);
        super.setTransferringAccountCommitmentPeriod(this.transferringAccountCommitmentPeriod);
        super.setStarted(this.started);
        super.setLastUpdated(this.lastUpdated);
        super.setItlNotification(this.itlNotificationSummary);
        super.setBlocks(this.blocks);
    }
}
