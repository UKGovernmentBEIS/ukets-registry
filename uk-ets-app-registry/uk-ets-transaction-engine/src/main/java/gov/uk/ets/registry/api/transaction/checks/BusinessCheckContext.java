package gov.uk.ets.registry.api.transaction.checks;

import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;


/**
 * Encapsulates the business context, i.e. a place to store objects and add errors.
 */
@Getter
@Setter
@NoArgsConstructor
public class BusinessCheckContext implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 3771377969362754795L;

    /**
     * The errors.
     */
    private List<BusinessCheckError> errors;

    /**
     * The store.
     */
    private Map<String, Serializable> store = new HashMap<>();

    /**
     * Constructor.
     *
     * @param transaction The transaction details.
     */
    public BusinessCheckContext(TransactionSummary transaction) {
        store.put(TransactionSummary.class.getSimpleName(), transaction);
    }

    /**
     * Retrieves the transaction details.
     *
     * @return the transactions details
     */
    public TransactionSummary getTransaction() {
        return (TransactionSummary) store.get(TransactionSummary.class.getSimpleName());
    }

    /**
     * Retrieves the transaction type.
     *
     * @return the transaction type.
     */
    public TransactionType getTransactionType() {
        return getTransaction().getType();
    }

    /**
     * Utility method for to retrieve the transaction's blocks.
     *
     * @return blocks
     */
    public List<TransactionBlockSummary> getBlocks() {

        List<TransactionBlockSummary> blocks = new ArrayList<>();
        Optional.ofNullable(getTransaction().getBlocks())
            .ifPresent(blocks::addAll);
        return blocks;
    }

    /**
     * Adds a business error.
     *
     * @param code    The code.
     * @param message The message.
     */
    public void addError(Integer code, String message) {
        if (code == null || StringUtils.isBlank(message)) {
            throw new IllegalArgumentException("Empty error code or message provided");
        }
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new BusinessCheckError(code, message));
    }

    /**
     * Retrieves an entry from the business context.
     *
     * @param key  The entry key.
     * @param type The entry type.
     * @param <T>  The parameterized type.
     * @return the entry
     */
    public <T> T get(String key, Class<T> type) {
        return (T) store.get(key);
    }

    /**
     * Stores an entry to the business context.
     *
     * @param key   The entry key.
     * @param entry The entry.
     */
    public void store(String key, Serializable entry) {
        store.put(key, entry);
    }

    /**
     * Key for acquiring account.
     */
    public static final String ACQUIRING_ACCOUNT = "ACQUIRING_ACCOUNT";

    /**
     * Key for transferring account.
     */
    public static final String TRANSFERRING_ACCOUNT = "TRANSFERRING_ACCOUNT";

    /**
     * Key for to be replaced units account.
     */
    public static final String TO_BE_REPLACED_UNITS_ACCOUNT = "TO_BE_REPLACED_UNITS_ACCOUNT";
    
    /**
     * Key for ITL Notice.
     */
    public static final String ITL_NOTICE = "ITL_NOTICE";
    
    /**
     * Whether the initiator has the permission to propose this transaction.
     */
    public static final String PERMISSION = "PERMISSION";

    /**
     * Key for the Carry over start date.
     */
    public static final String CARRY_OVER_START_DATE = "CARRY_OVER_START_DATE";

    /**
     * Key for the Carry over end date.
     */
    public static final String CARRY_OVER_END_DATE = "CARRY_OVER_END_DATE";

    /**
     * Checks whether the business context contains a specific error.
     *
     * @param errorCode The error code.
     * @return false/true
     */
    public boolean hasError(Integer errorCode) {
        boolean result = false;
        if (!CollectionUtils.isEmpty(errors)) {
            for (BusinessCheckError error : errors) {
                if (error.getCode().equals(errorCode)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Checks whether the business context contains any error.
     *
     * @return false/true
     */
    public boolean hasError() {
        return !CollectionUtils.isEmpty(errors);
    }

    /**
     * Checks whether transferring account checks are specified for this transaction type.
     *
     * @return false/true
     */
    public boolean skipTransferringAccountChecks() {
        return CollectionUtils.isEmpty(getTransaction().getType().getTransferringAccountTypes()) ||
            Constants.isInboundTransaction(getTransaction());
    }

    /**
     * Whether the initiator has the appropriate permission to invoke this transaction.
     *
     * @return false/true
     */
    public boolean hasPermission() {
        return Boolean.TRUE.equals(get(PERMISSION, Boolean.class));
    }

    /**
     * Resets this business check context.
     */
    public void reset() {
        errors = null;
        store = new HashMap<>();
    }
}
