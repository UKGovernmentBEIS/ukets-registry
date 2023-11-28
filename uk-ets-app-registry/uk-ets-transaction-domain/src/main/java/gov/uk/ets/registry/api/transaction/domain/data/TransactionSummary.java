package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.domain.type.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import static gov.uk.ets.commons.logging.RequestParamType.TRANSACTION_ID;

/**
 * Represents a summary of a transaction.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"identifier"})
@ToString
@Builder(builderClassName = "transactionSummaryBuilder", builderMethodName = "transactionSummaryBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummary implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -4192453403381624717L;

    /**
     * The unique business identifier, e.g. GB40140.
     */
    @MDCParam(TRANSACTION_ID)
    private String identifier;

    /**
     * The unique business identifier of the transaction to be reversed.
     */
    private String reversedIdentifier;

    /**
     * The summary of the relationship between the original and the reversal transaction.
     */
    private TransactionConnectionSummary transactionConnectionSummary;

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
     * Account holding the lCERs or tCERs to be replaced during a replacement: The full account identifier, e.g. GB-zzz-zzzzz-z-zz etc.
     */
    private String toBeReplacedBlocksAccountFullIdentifier;

    /**
     * Acquiring account: The commitment period, e.g. 0
     */
    private Integer acquiringAccountCommitmentPeriod;

    /**
     * The display name of the acquiring account.
     */
    private String acquiringAccountName;

    /**
     * Flag to determine if the account is external.
     */
    private boolean isExternalAcquiringAccount;

    /**
     * Flag to determine if the user has access to the acquiring account.
     */
    private boolean hasAccessToAcquiringAccount;

    /**
     * The status of the acquiring account.
     */
    private AccountStatus acquiringAccountStatus;

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
     * The display name of the transferring account.
     */
    private String transferringAccountName;

    /**
     * The status of the transferring account.
     */
    private AccountStatus transferringAccountStatus;


    /**
     * Flag to determine if the account is external.
     */
    private boolean isExternalTransferringAccount;

    /**
     * Flag to determine if the user has access to the transferring account.
     */
    private boolean hasAccessToTransferringAccount;

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
    private ItlNotificationSummary itlNotification;

    /**
     * The transaction blocks.
     */
    private List<TransactionBlockSummary> blocks;

    /**
     * The transaction responses.
     */
    private List<TransactionResponseSummary> responses;

    /**
     * The unit types involved in this transaction.
     * <ol>
     *     <li>Multiple, if many unit types are transacted.</li>
     *     <li>A specific unit type otherwise.</li>
     * </ol>
     */
    private UnitType unitType;

    /**
     * The task request identifier of the transaction.
     */
    private Long taskIdentifier;

    /**
     * A comment associated to the transaction.
     */
    private String comment;

    /**
     * Additional attributes.
     */
    private Map<String, Serializable> additionalAttributes;
    private String attributes;

    /**
     * The allocation type.
     */
    private AllocationType allocationType;

    /**
     * The allocation year.
     */
    private Integer allocationYear;

    /**
     * Whether the initial transaction can be reversed.
     */
    private boolean canBeReversed;

    /**
     * The transaction reference.
     */
    private String reference;

    /**
     * The planned transaction execution Date Time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private ZonedDateTime executionDateTime;

    /**
     * Calculates the total transacted quantity.
     *
     * @return the transacted quantity.
     */
    public Long calculateQuantity() {
        Long result = 0L;
        if (!CollectionUtils.isEmpty(blocks)) {
            for (TransactionBlockSummary block : blocks) {
                if (block.getEndBlock() != null && block.getStartBlock() != null) {
                    result += block.getEndBlock() - block.getStartBlock() + 1;
                } else {
                    result += block.calculateQuantity();
                }
            }
        }
        return result;
    }

    /**
     * Calculates the transacted quantity of the provided unit type.
     *
     * @return the transacted quantity.
     */
    public Long calculateQuantity(UnitType unitType) {
        Long result = 0L;
        if (!CollectionUtils.isEmpty(blocks)) {
            for (TransactionBlockSummary block : blocks) {
                if (block.getType().equals(unitType)) {
                    if (block.getEndBlock() != null && block.getStartBlock() != null) {
                        result += block.getEndBlock() - block.getStartBlock() + 1;
                    } else {
                        result += block.calculateQuantity();
                    }
                }
            }
        }
        return result;
    }

    /**
     * Calculates the unit types involved during this transaction.
     *
     * @return Multiple, if multiple unit types involved, a specific unit type otherwise.
     */
    public UnitType calculateUnitTypes() {
        UnitType result = null;
        Set<UnitType> unitTypes = new HashSet<>();
        if (!CollectionUtils.isEmpty(blocks)) {
            for (TransactionBlockSummary block : blocks) {
                unitTypes.add(block.getType());
            }
            if (unitTypes.size() > 1) {
                result = UnitType.MULTIPLE;

            } else {
                result = unitTypes.iterator().next();
            }
        }
        return result;
    }

}
